-- Backs Parent.children (@ManyToMany @JoinTable(name = "parent_students")) — links a parent
-- account to the student(s) they can see attendance/grades/fees for.
CREATE TABLE IF NOT EXISTS parent_students (
    parent_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    CONSTRAINT parent_students_pkey PRIMARY KEY (parent_id, student_id),
    CONSTRAINT fk_parent_students_parent FOREIGN KEY (parent_id) REFERENCES parents (user_id),
    CONSTRAINT fk_parent_students_student FOREIGN KEY (student_id) REFERENCES students (user_id)
);
