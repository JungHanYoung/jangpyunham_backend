package io.onezero.jangpyunham.domain.purchase;

import io.onezero.jangpyunham.domain.BaseEntity;
import io.onezero.jangpyunham.domain.grocery.Grocery;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "purchase")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private boolean completed;

    @ManyToOne(fetch = FetchType.EAGER)
    private Grocery grocery;
}
