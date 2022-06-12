package com.temperature.calculator;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface CalculatorRepo extends CrudRepository<Calculator, Timestamp> {



}
