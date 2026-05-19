package game.integration_project1_zaroc.model.ai;

import ai.onnxruntime.*;
import game.integration_project1_zaroc.model.boardinfo.Pawn;
import game.integration_project1_zaroc.model.boardinfo.Peg;
import game.integration_project1_zaroc.model.gameinfo.PawnColor;
import game.integration_project1_zaroc.model.gamelogic.Game;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZarocNeuralNet {

    private OrtEnvironment onnxEnv;
    private OrtSession onnxSession;
    private boolean isAvailable;

    private static final int[][] VALID_PEGS = {
            {1, 3, 5, 7},
            {1, 3, 5, 7},
            {0, 2, 4, 6, 8},
            {0, 2, 4, 6, 8}
    };
    private static final int[] MAX_STACK_PER_ROW = {4, 3, 2, 1};

    /**
     * the constructor initialises the model, here we start a OrtEnv , this is part of the ONNX dependency
     * and it lets you run a neural network in java , to use it we need to read the model in as bytes (bytes array)
     * then we start the environment and compile the model , we set the threads at 1 because its lightweight and works smooth like this
     * @param modelPath
     */
    public ZarocNeuralNet(String modelPath) {
        try {
            java.io.InputStream is = getClass().getResourceAsStream(modelPath);
            if (is == null) {
                throw new RuntimeException("ONNX Model not found: " + modelPath);
            }
            byte[] bytes = is.readAllBytes();
            onnxEnv = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            opts.setIntraOpNumThreads(1);
            onnxSession = onnxEnv.createSession(bytes, opts);
            isAvailable = true;
            System.out.println("Neural Network succesvol geladen!");
        } catch (Exception e) {
            System.err.println("Waarschuwing: Neuraal netwerk kon niet laden. Fallback wordt gebruikt.");
            isAvailable = false;
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Evaluates the given game state.
     * @return Double between 0.01 and 0.99 indicating the win probability for the given AI.
     */
    public double evaluateBoard(Game game, String aiUsername) {
        if (!isAvailable || onnxSession == null) return -1.0;

        try {
            float[] input = encodeBoard(game);
            long[] shape = {1, input.length};

            try (OnnxTensor tensor = OnnxTensor.createTensor(onnxEnv, FloatBuffer.wrap(input), shape)) {
                Map<String, OnnxTensor> inputs = new HashMap<>();
                inputs.put(onnxSession.getInputNames().iterator().next(), tensor);

                try (OrtSession.Result result = onnxSession.run(inputs)) {
                    float[][] output2d = (float[][]) result.get(0).getValue();
                    float[] output = output2d[0];

                    double prob = (output[0] + 1.0) / 2.0;

                    boolean isAiTurn = game.getCurrentTurn().getCurrentPlayer().getUsername()
                            .trim().equalsIgnoreCase(aiUsername);

                    double aiProb = isAiTurn ? prob : (1.0 - prob);

                    return Math.max(0.01, Math.min(0.99, aiProb));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1.0;
        }
    }

    /**
     * this method is used to encode the board for the neural network to make a prediction on
     * how good the game state is , its a value network so the input look like this:
     * [peg1 + isFull][peg2 + isFull]...[peg18 + isFull][current player]
     * the data is presented as -1 0 and 1
     * -1 = color player 1
     * 0 = empty
     * 1 = color player 2
     * and for the isFull flag we use 1 and 0 where 1 is true and 0 is false
     *
     * @param game
     * @return encodedBoard , this is just an array of floats because in order to use ONNX you need
     * to use floats
     */
    public static float[] encodeBoard(Game game) {
        PawnColor p1Color = game.getParticipation1().getChosenPawnColor();
        float[] encodedBoard = new float[91];
        int index = 0;

        for (int row = 0; row < VALID_PEGS.length; row++) {
            for (int col : VALID_PEGS[row]) {
                Peg peg = game.getBoard().getPegPosition(row, col);
                int maxStack = MAX_STACK_PER_ROW[row];
                int size = 0;
                float[] slots = new float[4];

                if (peg != null && !peg.getPawns().isEmpty()) {
                    List<Pawn> pawns = peg.getPawns();
                    size = pawns.size();
                    for (int i = 0; i < pawns.size() && i < 4; i++) {
                        slots[i] = pawns.get(i).getPawnColor().equals(p1Color) ? 1f : -1f;
                    }
                }

                for (int i = 0; i < 4; i++) {
                    encodedBoard[index++] = slots[i];
                }
                encodedBoard[index++] = (size >= maxStack) ? 1f : 0f;
            }
        }

        boolean isP1Turn = game.getCurrentTurn().getCurrentPlayer().getUsername()
                .equals(game.getParticipation1().getPlayer().getUsername());
        encodedBoard[index] = isP1Turn ? 1f : -1f;

        return encodedBoard;
    }
}