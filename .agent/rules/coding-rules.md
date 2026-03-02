# Coding Rules

These rules apply to all code written in this repository. They are non-negotiable.

## Five-Line Limit

Functions must not exceed 5 lines. If a function grows beyond this, extract the excess into a well-named sub-function.

## Call or Delegate — Not Both

A function either performs logic itself (call) or delegates to other functions (delegate). A function must not do both in the same body.

## `if` Statements at the Top Only

Guard clauses (`if`) must appear only at the beginning of a function. Do not place conditional logic in the middle or end of a function body.

## No `else`

Never use `else`. Return early or use polymorphism instead.

```java
// Bad
if (condition) {
    return foo();
} else {
    return bar();
}

// Good
if (condition) {
    return foo();
}
return bar();
```

## Inherit Only from Interfaces

Classes must not extend other classes. Inheritance is permitted only by implementing interfaces.

## Pure Conditions

Condition expressions must be pure: no variable assignments, no exceptions thrown, no I/O interactions inside the condition itself.

```java
// Bad
if ((line = reader.readLine()) != null)
if (!(file = findFile(name)).exists())

// Good
String line = reader.readLine();
if (line != null)
```

## No Single-Implementation Interfaces

Do not create an interface if there is only one class that implements it. Interfaces exist to abstract over multiple implementations or to define a boundary.

## Entity Naming

Classes annotated with `@Entity` must use `Entity` as a suffix.

```java
// Bad
@Entity
public class Employee { ... }

// Good
@Entity
public class EmployeeEntity { ... }
```

## Enum Naming

All enum types must use `Type` as a suffix.

```java
// Bad
enum EmploymentStatus { ... }
enum Gender { ... }

// Good
enum EmploymentStatusType { ... }
enum GenderType { ... }
```
