package at.ac.tuwien.ifs.sge.agent.alpharisk.debug;

import ai.djl.Device;
import ai.djl.Model;
import at.ac.tuwien.ifs.sge.agent.alpharisk.MuZero;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;
import at.ac.tuwien.ifs.sge.agent.alpharisk.agent.fast.model.djl.blocks.atraining.MuZeroBlock;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.Map;
import java.util.stream.IntStream;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.agent.fast.model.Network.getDoubleValue;
import static at.ac.tuwien.ifs.sge.agent.alpharisk.agent.fast.model.Network.getEpoch;

public class LossExtractor {


    public static void listLossesForTrainedNetworks(MuZeroConfig config) throws IOException {
        MuZeroBlock block = new MuZeroBlock(config);

        StringWriter stringWriter = new StringWriter();


        try (CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.EXCEL.withDelimiter(';').withHeader("trainingStep", "totalLoss", "valueLoss", "policyLoss"))) {


            try (Model model = Model.newInstance(config.getModelName(), Device.gpu())) {
                model.setBlock(block);
                IntStream.range(1, 1000).forEach(
                        i -> {
                            try {
                                model.load(Paths.get(MuZero.getNetworksBasedir(config)), model.getName(), Map.of("epoch", i));
                                int epoch = getEpoch(model);
                                int trainingSteps = config.getNumberOfTrainingStepsPerEpoch() * epoch;
                                csvPrinter.printRecord(trainingSteps,
                                        NumberFormat.getNumberInstance().format(getDoubleValue(model, "MeanLoss")),
                                        NumberFormat.getNumberInstance().format(getDoubleValue(model, "MeanValueLoss")),
                                        NumberFormat.getNumberInstance().format(getDoubleValue(model, "MeanPolicyLoss"))
                                );
                            } catch (Exception e) {
                            }
                        }
                );
            }
        }

        System.out.println(stringWriter.toString());
    }


}
