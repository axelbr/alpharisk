package at.ac.tuwien.ifs.sge.agent.alpharisk.visualization;

import at.ac.tuwien.ifs.sge.agent.alpharisk.domain.RiskState;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BoardVisualization {

    private  static SVGDocument doc;
    private static Map<Integer, String> territoryNames = new HashMap<>(Map.of(
            0, "alaska",
            1, "alberta",
            2, "central_america",
            3, "eastern_united_states",
            4, "greenland"
    ));
    
    static {
        territoryNames.put(5, "northwest_territory");
        territoryNames.put(6, "ontario");
        territoryNames.put(7, "quebec");
        territoryNames.put(8, "western_united_states");
        territoryNames.put(9, "argentina");
        territoryNames.put(10, "brazil");
        territoryNames.put(11, "peru");
        territoryNames.put(12, "venezuela");
        territoryNames.put(13, "great_britain");
        territoryNames.put(14, "iceland");
        territoryNames.put(15, "northern_europe");
        territoryNames.put(16, "scandinavia");
        territoryNames.put(17, "southern_europe");
        territoryNames.put(18, "ukraine");
        territoryNames.put(19, "western_europe");
        territoryNames.put(20, "central_africa");
        territoryNames.put(21, "east_africa");
        territoryNames.put(22, "egypt");
        territoryNames.put(23, "madagascar");
        territoryNames.put(24, "north_africa");
        territoryNames.put(25, "south_africa");
        territoryNames.put(26, "afghanistan");
        territoryNames.put(27, "china");
        territoryNames.put(28, "india");
        territoryNames.put(29, "irkutsk");
        territoryNames.put(30, "japan");
        territoryNames.put(31, "kamchatka");
        territoryNames.put(32, "middle_east");
        territoryNames.put(33, "mongolia");
        territoryNames.put(34, "siam");
        territoryNames.put(35, "siberia");
        territoryNames.put(36, "ural");
        territoryNames.put(37, "yakutsk");
        territoryNames.put(38, "eastern_australia");
        territoryNames.put(39, "indonesia");
        territoryNames.put(40, "new_guinea");
        territoryNames.put(41, "western_australia");
        URI inputUri = new File("./board_template.svg").toURI();
        try {
            doc = new SAXSVGDocumentFactory(XMLResourceDescriptor.getXMLParserClassName()).createSVGDocument(inputUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<Integer, String> playerColors = Map.of(
            -1, "white",
            0, "red",
    1, "green",
    2, "blue",
    3, "black",
    4, "yellow",
    5, "pink"
    );
    

    public static void saveBoard(String filename, RiskState state) {
        try {

            //SVGDocument is org.w3c.dom.Interface extending Document
            for (var tid : state.getBoard().getTerritories().keySet()) {
                var territory = state.getBoard().getTerritories().get(tid);
                var territoryInfo = doc.getElementById("territory:"+tid);
                var territoryPath = doc.getElementById(territoryNames.getOrDefault(tid, "alaska"));
                territoryPath.setAttribute("style", territoryPath.getAttribute("style") + ";fill:white");
                String text = String.format("\n%d:%d", territory.getOccupantPlayerId(), territory.getTroops());
                if (territoryInfo != null) {
                    territoryInfo.getLastChild().setTextContent(text);
                }
                var color = playerColors.get(territory.getOccupantPlayerId());
                territoryPath.setAttribute("style", territoryPath.getAttribute("style") + ";fill:"+color);


            }

            Transcoder transcoder = new SVGTranscoder();
            TranscoderInput input = new TranscoderInput(doc);
            try (OutputStream os = new FileOutputStream(filename)) {
                outputSvg(input, new File(filename));
            } catch (TranscoderException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Output in PNG
    private static void outputPng(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        PNGTranscoder t = new PNGTranscoder();
        try (OutputStream os = new FileOutputStream(outFile)) {
            TranscoderOutput output = new TranscoderOutput(os);
            t.transcode(input, output);
        }
    }

    //Output in JPEG
    private static void outputJpg(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        JPEGTranscoder t = new JPEGTranscoder();
        t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, .8f);
        try (OutputStream os = new FileOutputStream(outFile)) {
            TranscoderOutput output = new TranscoderOutput(os);
            t.transcode(input, output);
        }
    }

    //Output as SVG
    private static void outputSvg(TranscoderInput input, File outFile) throws IOException, TranscoderException {
        SVGTranscoder t = new SVGTranscoder();
        try (OutputStream os = new FileOutputStream(outFile)) {
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            TranscoderOutput output = new TranscoderOutput(writer);
            t.transcode(input, output);
        }
    }

}
