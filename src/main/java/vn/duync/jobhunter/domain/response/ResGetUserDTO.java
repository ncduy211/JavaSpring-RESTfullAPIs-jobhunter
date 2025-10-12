package vn.duync.jobhunter.domain.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.duync.jobhunter.util.constant.GenderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResGetUserDTO {
    private Long id;

    private String name;

    private String email;

    private int age;

    private GenderEnum gender;

    private String address;

    private Instant createAt;

    private Instant updateAt;

}