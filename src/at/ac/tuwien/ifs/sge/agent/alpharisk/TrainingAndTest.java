package at.ac.tuwien.ifs.sge.agent.alpharisk;

import at.ac.tuwien.ifs.sge.agent.alpharisk.config.MuZeroConfig;
import at.ac.tuwien.ifs.sge.agent.alpharisk.config.RiskConfigFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;

import static at.ac.tuwien.ifs.sge.agent.alpharisk.MuZero.train;

@Slf4j
public class TrainingAndTest {

    public static void main(String[] args) throws URISyntaxException, IOException {
        int size = 9;

        MuZeroConfig config = RiskConfigFactory.getRiskInstance();
        String dir = "./memory/risk/";
        config.setOutputDir(dir);

//        FileUtils.deleteDirectory(new File(dir));

        boolean freshBuffer = false;
        int numberOfEpochs = 1;
        train(config, freshBuffer, numberOfEpochs, false, false);

    }


}
