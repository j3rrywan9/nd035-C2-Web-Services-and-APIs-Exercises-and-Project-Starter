package com.udacity.pricing.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Entity
public class Price {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "VEHICLEID")
  private Long vehicleId;
  private String currency;
  private BigDecimal price;
}
