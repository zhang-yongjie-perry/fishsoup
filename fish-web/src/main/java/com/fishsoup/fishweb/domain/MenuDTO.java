package com.fishsoup.fishweb.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MenuDTO implements Comparable<MenuDTO>, Serializable {
    @Serial
    private static final long serialVersionUID = -2700439769210038130L;
    private String id;
    private String menuId;
    private String name;
    private String url;
    private String remark;
    private Integer sort;
    private String display;

    @Override
    public int compareTo(MenuDTO menuDTO) {
        return this.sort.compareTo(menuDTO.getSort());
    }
}
