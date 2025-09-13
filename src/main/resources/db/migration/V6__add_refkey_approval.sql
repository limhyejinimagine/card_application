ALTER TABLE approval
      ADD CONSTRAINT fk_approval_application
      FOREIGN KEY (application_id)
      REFERENCES card_application(application_id);
