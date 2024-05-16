package ro.axon.dot.service;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidGeneratorHandler {

  public String generateUuid(){
    return UUID.randomUUID().toString();
  }
}