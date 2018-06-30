package com.crossover.techtrial.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DailyElectricity class will hold sum, average,minimum and maximum electricity for a given day.
 * @author Crossover
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DailyElectricity implements Serializable {
  
  private static final long serialVersionUID = 3605549122072628877L;

  private LocalDate date;
  
  private Long sum;
  
  private Double average;
  
  private Long min;
  
  private Long max;

}
