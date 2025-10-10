package vn.duync.jobhunter.domain.dto.resuserDTO;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.duync.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
public class ResCreateUserDTO {
    private Long id;

    private String name;

    private String email;

    private int age;

    private GenderEnum gender;

    private String address;

    private Instant createAt;

}
