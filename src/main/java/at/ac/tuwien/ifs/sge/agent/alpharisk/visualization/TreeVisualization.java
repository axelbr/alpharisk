package at.ac.tuwien.ifs.sge.agent.alpharisk.visualization;

import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.ChanceNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.DecisionNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.Node;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.nodes.NodeWrapper;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static guru.nidi.graphviz.model.Factory.*;

public class TreeVisualization {

   private static Map<Integer, Color> playerColors  = Map.of(
           0, Color.WHITE.fill(),
           1, Color.GRAY.fill()
   );

    private static MutableGraph toDotGraph(Node node) {
        MutableGraph g = mutGraph("Search Tree").setDirected(true)
                        .add(buildGraph(node));
        return g;
    }

    public static void save(Node node, String path, Format format) {
        var g = toDotGraph(node);
        try {
            Graphviz.fromGraph(g).width(200).render(format).toFile(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String toDotString(Node node) {
        return toDotGraph(node).toString();
    }

    public static void saveToSvg(String path, Node node) {
        var g = toDotGraph(node);
        try {
            Graphviz.fromGraph(g).width(200).render(Format.SVG).toFile(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveToDotFile(String path, Node node) {
        var g = toDotGraph(node);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(g.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MutableNode buildGraph(Node node) {
        while (node instanceof NodeWrapper) {
            node = ((NodeWrapper) node).unwrapped();
        }
        if (node.isLeaf()) {
            return toGraphNode(node);
        } else {
            var graphNode = toGraphNode(node);
            for (var child: node.expandedChildren()) {
                graphNode.addLink(to(buildGraph(child)).add(edgeLabel(node, child)));

            }
            return graphNode;
        }
    }

    private static Attributes<ForAll> edgeLabel(Node parent, Node child) {
        if (parent instanceof DecisionNode) {
            return edgeLabel((DecisionNode) parent, child);
        } else if (parent instanceof ChanceNode) {
            return edgeLabel((ChanceNode) parent, child);
        } else {
            throw new IllegalArgumentException("Unexpected type.");
        }
    }

    private static Attributes<ForAll> edgeLabel(ChanceNode parent, Node child) {
        String chance = parent.getOutcome(child).toString();
        return Attributes.attrs(Label.of(chance));
    }

    private static Attributes<ForAll> edgeLabel(DecisionNode parent, Node child) {
        return Attributes.attrs(Label.of(child.getAction().toString()));
    }

    private static MutableNode toGraphNode(Node node) {
        String label = getLabel(node);
        var mutNode = mutNode(label);
        if (node instanceof DecisionNode) {
            mutNode = mutNode.add(Shape.DIAMOND);
        } else if (node instanceof ChanceNode) {
            mutNode = mutNode(label);
        } else {
            mutNode = mutNode(label);
        }
        return mutNode.add(playerColors.get(node.getState().getCurrentPlayer()), Style.FILLED);
    }

    private static String getLabel(Node node) {
        return String.format("%s\n@%s", node, Integer.toHexString(node.hashCode()));
    }
}
