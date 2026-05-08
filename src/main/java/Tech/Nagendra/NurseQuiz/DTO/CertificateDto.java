package Tech.Nagendra.NurseQuiz.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateDto {
    private String name;
    private String status;
    private String downloadUrl;
}