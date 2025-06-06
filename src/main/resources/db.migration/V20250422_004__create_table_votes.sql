CREATE TABLE votes
(
    vote_id      BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    user_id      BIGINT                                  NOT NULL,
    candidate_id BIGINT                                  NOT NULL,
    party_id     BIGINT                                  NOT NULL,
    time_stamp   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    CONSTRAINT pk_votes PRIMARY KEY (vote_id)
);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_CANDIDATE FOREIGN KEY (candidate_id) REFERENCES candidates (candidate_id);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_PARTY FOREIGN KEY (party_id) REFERENCES parties (party_id);

ALTER TABLE votes
    ADD CONSTRAINT FK_VOTES_ON_USER FOREIGN KEY (user_id) REFERENCES users (user_id);