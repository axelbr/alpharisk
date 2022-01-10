package at.ac.tuwien.ifs.sge.agent.alpharisk.tree;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.chance.ChanceNode;
import at.ac.tuwien.ifs.sge.agent.alpharisk.tree.decision.DecisionNode;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.*;

import static guru.nidi.graphviz.model.Factory.*;

public class TreeVisualization {
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
        if (node.isLeaf()) {
            return toGraphNode(node);
        } else {
            var graphNode = toGraphNode(node);
            for (var child: node.getChildren()) {
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
        String chance = String.format("%.2f%%", parent.getProbability(child.getState()) * 100);
        return Attributes.attrs(Label.of(chance));
    }

    private static Attributes<ForAll> edgeLabel(DecisionNode parent, Node child) {
        return Attributes.attrs(Label.of(child.getAction().get().toString()));
    }

    private static MutableNode toGraphNode(Node node) {
        String label = getLabel(node);
        if (node instanceof DecisionNode) {
            return mutNode(label).add(Shape.DIAMOND);
        } else if (node instanceof ChanceNode) {
            return mutNode(label);
        } else {
            return mutNode(label);
        }
    }

    private static String getLabel(Node node) {
        String label = String.format("%s@%s", node, Integer.toHexString(node.hashCode()));
        return label;
    }
}
