package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;

public class CarbonCalculatorService {
    
    public StartCalcResponseDTO startCalculation(StartCalcRequestDTO request) {
        StartCalcResponseDTO response = new StartCalcResponseDTO();
        
        return response;
    }
    
    public UpdateCalcInfoResponseDTO updateInfo(UpdateCalcInfoRequestDTO request)  {
        UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();

        return response;
    }

    public CarbonCalculationResultDTO getResult(String id) {
        CarbonCalculationResultDTO result = new CarbonCalculationResultDTO();


        return result;
    }

}
