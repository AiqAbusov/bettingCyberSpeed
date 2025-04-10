package service;

import dto.response.ScratcherResponseDTO;

public interface ScratcherService {

    String scratch(String[][] scratch,Double bettingAmount);
}
