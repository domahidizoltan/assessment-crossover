package com.crossover.techtrial.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Panel class hold information related to a Solar panel.
 * 
 * @author Crossover
 *
 */
@Entity
@Table(name = "panel")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Panel implements Serializable {

  private static final long serialVersionUID = -8527695980909864257L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @NotNull
  @EqualsAndHashCode.Include
  private String serial;

  private Double longitude;

  private Double latitude;

  @Nullable
  @EqualsAndHashCode.Include
  private String brand;

  @Column(name = "created_at")
  @JsonIgnore
  private LocalDateTime createdAt;

}
