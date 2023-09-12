
DROP FUNCTION if exists random_age(age_in INTEGER);

CREATE OR REPLACE FUNCTION random_age(age_in INTEGER)
    RETURNS INTEGER AS '
DECLARE
    random_offset INTEGER;
BEGIN
    -- 입력값 검사
    IF age_in < 0 OR age_in > 150 THEN
        RAISE EXCEPTION ''Invalid age value: %'', age_in;
    END IF;

    random_offset := CAST(FLOOR(RANDOM() * 21) - 10 AS INTEGER);
    RETURN age_in + random_offset;
END;
' LANGUAGE plpgsql;