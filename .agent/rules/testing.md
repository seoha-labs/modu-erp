# Testing Rules

## Test Layers & File Naming

| Layer | Suffix | Scope | Spring Context |
|---|---|---|---|
| Unit | `*Test.java` | Single class, no I/O | None |
| Slice | `*Test.java` | One layer (web/jpa) | Partial (`@WebMvcTest`, `@DataJpaTest`) |
| Integration | `*IT.java` | Full flow within a service | Full (`@SpringBootTest`) |
| E2E | `*E2ETest.java` | API contract across gateway | Full + Testcontainers |

Unit tests are the default. Write integration and E2E tests only where unit tests cannot cover the behavior (e.g., DB queries, HTTP routing).

## Naming Convention

```java
@DisplayName("Given valid vacation request, when submitted, then returns 201 with location header")
@Test
void givenValidRequest_whenSubmit_thenReturns201WithLocation() { ... }
```

- Method name: `given{Context}_when{Action}_then{Outcome}` — use camelCase within each segment.
- `@DisplayName`: Full English sentence. Starts with "Given", reads naturally.

## Test Structure

Always separate the three phases with comments.

```java
@Test
void givenApprovedVacation_whenCancel_thenThrowsException() {
    // given
    Vacation vacation = VacationFixture.approved();

    // when
    ThrowableAssert.ThrowingCallable cancel = () -> vacation.cancel();

    // then
    assertThatThrownBy(cancel)
        .isInstanceOf(IllegalStateException.class);
}
```

## Assertions

Use **AssertJ** exclusively for all assertions.

```java
// Value equality
assertThat(result.name()).isEqualTo("John");

// Collections
assertThat(list).hasSize(3).contains(item);

// Exceptions
assertThatThrownBy(() -> subject.act())
    .isInstanceOf(SomeException.class)
    .hasMessageContaining("reason");

// Booleans
assertThat(vacation.isPending()).isTrue();
```

For `MockMvc`, avoid Hamcrest matchers. Use `.value()` or extract the response and assert with AssertJ.

```java
// Preferred: MockMvc built-in value matchers (no Hamcrest)
mockMvc.perform(get("/api/hr/employees"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name").value("John"));

// Alternative: extract and assert with AssertJ
MvcResult result = mockMvc.perform(get("/api/vacation/1"))
    .andExpect(status().isOk())
    .andReturn();

assertThat(result.getResponse().getContentAsString()).contains("startDate");
```

## Mocking

Use **Mockito** to isolate the unit under test. Mock only direct collaborators.

```java
@ExtendWith(MockitoExtension.class)
class VacationSubmissionTest {

    @Mock
    ApprovalLines approvalLines;

    @InjectMocks
    VacationSubmission submission;

    @Test
    void givenNoApprover_whenSubmit_thenThrowsException() {
        // given
        given(approvalLines.findFor(any())).willReturn(List.of());

        // when
        ThrowableAssert.ThrowingCallable submit = () -> submission.submit(VacationFixture.draft());

        // then
        assertThatThrownBy(submit).isInstanceOf(NoApproverException.class);
    }
}
```

- Prefer `BDDMockito.given(...)` over `Mockito.when(...)` for consistency with Given/When/Then structure.
- Do not mock types you don't own (e.g., `HttpServletRequest`, `EntityManager`). Use slice tests instead.

## Test Fixtures

Place all fixtures in `src/test/java/com/modu/erp/{module}/fixture/`.

Use the **Object Mother** pattern — static factory methods that return ready-to-use objects.

```java
public class VacationFixture {

    public static Vacation draft() {
        return new Vacation(EmployeeFixture.active(), LocalDate.now(), LocalDate.now().plusDays(2));
    }

    public static Vacation approved() {
        Vacation vacation = draft();
        vacation.approve();
        return vacation;
    }
}
```

- One fixture class per domain concept.
- Do not use `@Builder` on production entities to serve tests — keep fixtures in fixture classes.

## Slice Tests

### Web Layer (`@WebMvcTest`)

```java
@WebMvcTest(VacationController.class)
class VacationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    VacationSubmission vacationSubmission;

    @Test
    void givenValidPayload_whenPost_thenReturns201() throws Exception {
        // given
        given(vacationSubmission.submit(any())).willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/vacation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"startDate\":\"2026-03-01\",\"endDate\":\"2026-03-03\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L));
    }
}
```

### Persistence Layer (`@DataJpaTest`)

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class VacationRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    VacationRepository vacationRepository;

    @Test
    void givenSavedVacation_whenFindByEmployee_thenReturnsIt() {
        // given
        Vacation saved = vacationRepository.save(VacationFixture.draft());

        // when
        List<Vacation> result = vacationRepository.findByEmployeeId(saved.employeeId());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(saved.id());
    }
}
```

- Always use **Testcontainers** with real PostgreSQL. Do not use H2.

## Integration Tests (`*IT.java`)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class VacationSubmissionIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    TestRestTemplate restTemplate;
}
```

## Grouping with `@Nested`

Use `@Nested` to group tests by scenario within a single test class.

```java
class VacationTest {

    @Nested
    @DisplayName("When submitting a vacation request")
    class Submit {

        @Test
        void givenDraftVacation_whenSubmit_thenStatusChangesToPending() { ... }

        @Test
        void givenAlreadySubmitted_whenSubmit_thenThrowsException() { ... }
    }

    @Nested
    @DisplayName("When cancelling a vacation request")
    class Cancel {
        ...
    }
}
```

## What Not To Do

- Do not use Hamcrest matchers anywhere — use AssertJ instead.
- Do not load the full Spring context (`@SpringBootTest`) for unit or web-layer tests.
- Do not modify production code solely to make it testable.
- Do not assert on log output or internal implementation details — assert on observable behavior only.
- Do not share mutable state between tests. Each test must be fully independent.
