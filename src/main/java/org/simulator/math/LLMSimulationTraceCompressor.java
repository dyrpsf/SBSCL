package org.simulator.math;

import org.simulator.math.odes.MultiTable;

/**
 * A utility designed to compress high-density simulation time-series data
 * into token-efficient summaries for Large Language Model (LLM) consumption.
 * Part of the gsoc-sysbio-llm-tools initiative.
 */
public class LLMSimulationTraceCompressor {

    /**
     * Compresses a full simulation MultiTable into a token-optimized summary.
     * Extracts initial values, peak values, and final steady states.
     *
     * @param table The simulation results MultiTable
     * @return A token-efficient String representation for LLM prompts
     */
    public static String compressForLLM(MultiTable table) {
        if (table == null || table.getRowCount() == 0) {
            return "Simulation data is empty.";
        }

        int numRows = table.getRowCount();
        int numCols = table.getColumnCount();

        StringBuilder llmSummary = new StringBuilder();
        llmSummary.append("Simulation Summary (Token-Optimized):\n");
        llmSummary.append("Total Time Steps: ").append(numRows).append("\n");
        llmSummary.append("Time Range: [").append(table.getTimePoint(0))
                  .append(", ").append(table.getTimePoint(numRows - 1)).append("]\n\n");

        // Skip index 0 if it represents the Time column
        int startIdx = table.getColumnName(0).equalsIgnoreCase("time") ? 1 : 0;

        for (int i = startIdx; i < numCols; i++) {
            String species = table.getColumnName(i);
            
            // Extract values using AbstractTableModel's getValueAt
            double initial = ((Number) table.getValueAt(0, i)).doubleValue();
            double finalVal = ((Number) table.getValueAt(numRows - 1, i)).doubleValue();
            double max = initial;
            double min = initial;
            double timeOfMax = table.getTimePoint(0);

            for (int j = 0; j < numRows; j++) {
                double val = ((Number) table.getValueAt(j, i)).doubleValue();
                if (val > max) {
                    max = val;
                    timeOfMax = table.getTimePoint(j);
                }
                if (val < min) {
                    min = val;
                }
            }

            llmSummary.append(String.format("- %s: Init:%.2f | Final:%.2f | Peak:%.2f @ t=%.2f | Min:%.2f\n", 
                                            species, initial, finalVal, max, timeOfMax, min));
        }

        return llmSummary.toString();
    }
}