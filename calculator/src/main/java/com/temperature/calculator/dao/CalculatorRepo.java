package com.temperature.calculator.dao;

import com.temperature.calculator.dao.Calculator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface CalculatorRepo extends CrudRepository<Calculator, Timestamp> {



}
