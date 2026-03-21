CREATE TABLE annual_leave_policies (
    id                      BIGSERIAL    PRIMARY KEY,
    country_code            VARCHAR(2)   NOT NULL,
    initial_vacation_hours  INTEGER      NOT NULL,
    annual_vacation_hours   INTEGER      NOT NULL,
    effective_date          DATE         NOT NULL,
    created_at              TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uq_annual_leave_country_effective UNIQUE (country_code, effective_date)
);

CREATE INDEX idx_annual_leave_policies_country_effective
    ON annual_leave_policies (country_code, effective_date DESC);

CREATE TABLE tenure_bonuses (
    id                        BIGSERIAL    PRIMARY KEY,
    annual_leave_policy_id    BIGINT       NOT NULL REFERENCES annual_leave_policies(id),
    required_tenure_years     INTEGER      NOT NULL,
    bonus_hours               INTEGER      NOT NULL,
    max_total_hours           INTEGER,
    CONSTRAINT uq_tenure_bonus_policy_years UNIQUE (annual_leave_policy_id, required_tenure_years)
);

CREATE INDEX idx_tenure_bonuses_policy_id
    ON tenure_bonuses (annual_leave_policy_id);
