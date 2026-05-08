package Tech.Nagendra.NurseQuiz.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "batches")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batch_id;


    private String level;

    private String start_date;
    private String start_time;
    private String end_date;
    private String end_time;

    private boolean random_photo;
    private boolean random_video;
    private boolean ai_monitoring;

    private boolean tab_switch_detection;
    private int max_tab_switches;

    @Column(name = "batch_code", unique = true)
    private String batchCode;

    private String status;
    @Column(name = "duration")
    private int duration;

    private int total_questions;
    private int enrolled_students;


}
