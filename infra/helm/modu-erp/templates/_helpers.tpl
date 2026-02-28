{{/*
Expand the name of the chart.
*/}}
{{- define "modu-erp.name" -}}
{{- .Chart.Name | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "modu-erp.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Selector labels for a given service
Usage: include "modu-erp.selectorLabels" (dict "name" "erp-hr")
*/}}
{{- define "modu-erp.selectorLabels" -}}
app.kubernetes.io/name: {{ .name }}
app.kubernetes.io/instance: {{ .release }}
{{- end }}

{{/*
Common environment variables injected into all services
*/}}
{{- define "modu-erp.commonEnv" -}}
- name: DB_HOST
  value: {{ .Values.database.host | quote }}
- name: DB_PORT
  value: {{ .Values.database.port | quote }}
- name: DB_USERNAME
  value: {{ .Values.database.username | quote }}
- name: DB_PASSWORD
  valueFrom:
    secretKeyRef:
      name: modu-erp-db-secret
      key: password
- name: KEYCLOAK_ISSUER_URI
  value: {{ .Values.keycloak.issuerUri | quote }}
{{- end }}
