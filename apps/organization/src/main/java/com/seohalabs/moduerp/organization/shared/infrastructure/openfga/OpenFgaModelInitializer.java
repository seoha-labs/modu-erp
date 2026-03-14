package com.seohalabs.moduerp.organization.shared.infrastructure.openfga;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.openfga.sdk.api.client.OpenFgaClient;
import dev.openfga.sdk.api.model.CreateStoreRequest;
import dev.openfga.sdk.api.model.WriteAuthorizationModelRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(0)
@Component
@RequiredArgsConstructor
public class OpenFgaModelInitializer implements ApplicationRunner {

  private final OpenFgaClient fgaClient;
  private final ObjectMapper objectMapper;

  @Value("${openfga.store-id:}")
  private String storeId;

  @Value("${openfga.authorization-model-id:}")
  private String modelId;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    if (storeId.isBlank()) {
      createStore();
      return;
    }
    ensureModel();
  }

  private void ensureModel() throws Exception {
    if (!modelId.isBlank()) {
      return;
    }
    createModel();
  }

  private void createStore() throws Exception {
    String id = fgaClient.createStore(new CreateStoreRequest().name("modu-erp")).get().getId();
    log.warn(
        "=== Initial startup. Set OPENFGA_STORE_ID={} as an environment variable and restart ===",
        id);
  }

  private void createModel() throws Exception {
    WriteAuthorizationModelRequest request =
        objectMapper.readValue(MODEL_JSON, WriteAuthorizationModelRequest.class);
    String id = fgaClient.writeAuthorizationModel(request).get().getAuthorizationModelId();
    log.warn("=== Set OPENFGA_MODEL_ID={} as an environment variable and restart ===", id);
  }

  // language=json
  private static final String MODEL_JSON =
      """
      {
        "schema_version": "1.1",
        "type_definitions": [
          {"type": "user"},
          {
            "type": "system",
            "relations": {
              "admin":                {"this": {}},
              "hr_manager":           {"this": {}},
              "member":               {"union": {"child": [{"this": {}}, {"computedUserset": {"relation": "admin"}}, {"computedUserset": {"relation": "hr_manager"}}]}},
              "can_create_employee":  {"union": {"child": [{"computedUserset": {"relation": "admin"}}, {"computedUserset": {"relation": "hr_manager"}}]}},
              "can_list_employee":    {"union": {"child": [{"computedUserset": {"relation": "admin"}}, {"computedUserset": {"relation": "hr_manager"}}]}},
              "can_create_department":{"computedUserset": {"relation": "admin"}},
              "can_list_department":  {"computedUserset": {"relation": "member"}},
              "can_create_position":  {"computedUserset": {"relation": "admin"}},
              "can_list_position":    {"computedUserset": {"relation": "member"}},
              "can_create_role":      {"computedUserset": {"relation": "admin"}},
              "can_list_role":        {"computedUserset": {"relation": "member"}},
              "can_grant_hr_manager": {"union": {"child": [{"computedUserset": {"relation": "admin"}}, {"computedUserset": {"relation": "hr_manager"}}]}}
            },
            "metadata": {
              "relations": {
                "admin":      {"directly_related_user_types": [{"type": "user"}]},
                "hr_manager": {"directly_related_user_types": [{"type": "user"}]},
                "member":     {"directly_related_user_types": [{"type": "user"}]}
              }
            }
          },
          {
            "type": "employee",
            "relations": {
              "parent":    {"this": {}},
              "owner":     {"this": {}},
              "admin":      {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "admin"}}},
              "hr_manager": {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "hr_manager"}}},
              "can_view":   {"union": {"child": [{"computedUserset": {"relation": "owner"}}, {"computedUserset": {"relation": "admin"}}, {"computedUserset": {"relation": "hr_manager"}}]}},
              "can_resign": {"union": {"child": [{"computedUserset": {"relation": "admin"}}, {"computedUserset": {"relation": "hr_manager"}}]}}
            },
            "metadata": {
              "relations": {
                "parent": {"directly_related_user_types": [{"type": "system"}]},
                "owner":  {"directly_related_user_types": [{"type": "user"}]}
              }
            }
          },
          {
            "type": "department",
            "relations": {
              "parent":     {"this": {}},
              "admin":      {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "admin"}}},
              "can_create": {"computedUserset": {"relation": "admin"}},
              "can_list":   {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "member"}}}
            },
            "metadata": {
              "relations": {
                "parent": {"directly_related_user_types": [{"type": "system"}]}
              }
            }
          },
          {
            "type": "position",
            "relations": {
              "parent":     {"this": {}},
              "admin":      {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "admin"}}},
              "can_create": {"computedUserset": {"relation": "admin"}},
              "can_list":   {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "member"}}}
            },
            "metadata": {
              "relations": {
                "parent": {"directly_related_user_types": [{"type": "system"}]}
              }
            }
          },
          {
            "type": "role_resource",
            "relations": {
              "parent":     {"this": {}},
              "admin":      {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "admin"}}},
              "can_create": {"computedUserset": {"relation": "admin"}},
              "can_list":   {"tupleToUserset": {"tupleset": {"relation": "parent"}, "computedUserset": {"relation": "member"}}}
            },
            "metadata": {
              "relations": {
                "parent": {"directly_related_user_types": [{"type": "system"}]}
              }
            }
          }
        ]
      }
      """;
}
