package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.model.Calculation;
import br.com.actionlabs.carboncalc.repository.CalculationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCalculatorService {
    private final CalculationRepository calculationRepository;

    public StartCalcResponseDTO startCalculation(StartCalcRequestDTO request) {
        Calculation calculation = new Calculation();

        calculation.setName(request.getName());
        calculation.setEmail(request.getEmail());
        calculation.setPhoneNumber(request.getEmail());
        calculation.setUf(request.getUf());

        Calculation saved = calculationRepository.save(calculation);

        StartCalcResponseDTO response = new StartCalcResponseDTO();

        response.setId(saved.getId());
        
        return response;
    }
    
    public UpdateCalcInfoResponseDTO updateInfo(UpdateCalcInfoRequestDTO request)  {
        Calculation calculation = calculationRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Calculation not found: " + request.getId()));

        calculation.setEnergyConsumption(request.getEnergyConsumption());
        calculation.setTransportation(request.getTransportation());
        calculation.setSolidWasteTotal(request.getSolidWasteTotal());
        calculation.setRecyclePercentage(request.getRecyclePercentage());

        calculationRepository.save(calculation);

        UpdateCalcInfoResponseDTO response = new UpdateCalcInfoResponseDTO();

        response.setSuccess(true);

        return response;
    }

    public CarbonCalculationResultDTO getResult(String id) {
        CarbonCalculationResultDTO result = new CarbonCalculationResultDTO();


        return result;
    }

}
