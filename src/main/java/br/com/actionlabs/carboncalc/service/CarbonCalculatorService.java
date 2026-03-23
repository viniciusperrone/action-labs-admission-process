package br.com.actionlabs.carboncalc.service;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.model.Calculation;
import br.com.actionlabs.carboncalc.model.EnergyEmissionFactor;
import br.com.actionlabs.carboncalc.model.SolidWasteEmissionFactor;
import br.com.actionlabs.carboncalc.model.TransportationEmissionFactor;
import br.com.actionlabs.carboncalc.repository.CalculationRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonCalculatorService {

    private final CalculationRepository calculationRepository;
    private final EnergyEmissionFactorRepository energyEmissionFactorRepository;
    private final TransportationEmissionFactorRepository transportationEmissionFactorRepository;
    private final SolidWasteEmissionFactorRepository solidWasteEmissionFactorRepository;

    public StartCalcResponseDTO startCalculation(StartCalcRequestDTO request) {
        Calculation calculation = new Calculation();

        calculation.setName(request.getName());
        calculation.setEmail(request.getEmail());
        calculation.setPhoneNumber(request.getPhoneNumber());
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
         Calculation calculation = calculationRepository.findById(id)
                 .orElseThrow(() -> new IllegalArgumentException("Calculation not found: " + id));

         double energyEmission = calculateEnergyEmission(calculation);
         double transportationEmission = calculateTransportationEmission(calculation);
         double solidWasteEmission = calculateSolidWasteEmission(calculation);


        CarbonCalculationResultDTO result = new CarbonCalculationResultDTO();

        result.setEnergy(energyEmission);
        result.setTransportation(transportationEmission);
        result.setSolidWaste(solidWasteEmission);
        result.setTotal(energyEmission + transportationEmission + solidWasteEmission);

        return result;
    }

    private double calculateEnergyEmission(Calculation calculation) {
        if (calculation.getEnergyConsumption() == null) return 0;

        EnergyEmissionFactor factor = energyEmissionFactorRepository.findById(calculation.getUf())
                .orElseThrow(() -> new IllegalArgumentException("Energy emission factor not found for UF: " + calculation.getUf()));

        return calculation.getEnergyConsumption() * factor.getFactor();
    }

    private double calculateTransportationEmission(Calculation calculation) {
        if (calculation.getTransportation() == null || calculation.getTransportation().isEmpty()) return 0;

        return calculation.getTransportation().stream()
                .mapToDouble(t -> {
                    TransportationEmissionFactor factor = transportationEmissionFactorRepository.findById(t.getType())
                            .orElseThrow(() -> new IllegalArgumentException("Transportation emission factor not found for type: " + t.getType()));

                    return t.getMonthlyDistance() * factor.getFactor();
                })
                .sum();
    }

    private double calculateSolidWasteEmission(Calculation calculation) {
        if (calculation.getSolidWasteTotal() == null || calculation.getRecyclePercentage() == null) return 0;

        SolidWasteEmissionFactor factor = solidWasteEmissionFactorRepository.findById(calculation.getUf())
                .orElseThrow(() -> new IllegalArgumentException("Solid waste emission factor nout found for UF: " + calculation.getUf()));

        double totalWaste = calculation.getSolidWasteTotal();
        double recyclePercentage = calculation.getRecyclePercentage();

        double recyclableWaste = totalWaste * recyclePercentage;
        double nonRecyclableWaste = totalWaste * (1 - recyclePercentage);

        return (recyclableWaste * factor.getRecyclableFactor())
                + (nonRecyclableWaste * factor.getNonRecyclableFactor());
    }

}
