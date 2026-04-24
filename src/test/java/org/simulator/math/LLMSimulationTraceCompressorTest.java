package org.simulator.math;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.simulator.math.odes.MultiTable;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LLMSimulationTraceCompressorTest {

    @Test
    public void testCompressForLLM() {
        // Create a Mock MultiTable
        MultiTable mockTable = Mockito.mock(MultiTable.class);

        // Define the behavior of our mock table (3 time steps, 2 columns)
        Mockito.when(mockTable.getRowCount()).thenReturn(3);
        Mockito.when(mockTable.getColumnCount()).thenReturn(2);
        
        // Mock time points
        Mockito.when(mockTable.getTimePoint(0)).thenReturn(0.0);
        Mockito.when(mockTable.getTimePoint(1)).thenReturn(1.0);
        Mockito.when(mockTable.getTimePoint(2)).thenReturn(2.0);

        // Mock column names
        Mockito.when(mockTable.getColumnName(0)).thenReturn("Time");
        Mockito.when(mockTable.getColumnName(1)).thenReturn("Species_A");
        
        // Give Species A some data: Starts at 10, peaks at 50, ends at 5
        Mockito.when(mockTable.getValueAt(0, 1)).thenReturn(10.0);
        Mockito.when(mockTable.getValueAt(1, 1)).thenReturn(50.0);
        Mockito.when(mockTable.getValueAt(2, 1)).thenReturn(5.0);

        // Run your new compressor!
        String result = LLMSimulationTraceCompressor.compressForLLM(mockTable);

        // Print it to the console so you can see your own work
        System.out.println("=== AI TRACE OUTPUT ===");
        System.out.println(result);
        System.out.println("=======================");

        // Verify the output contains the correct compressed insights
        assertTrue(result.contains("Total Time Steps: 3"), "Should contain row count");
        assertTrue(result.contains("Species_A: Init:10.00"), "Should contain initial value");
        assertTrue(result.contains("Peak:50.00 @ t=1.00"), "Should contain peak value and exact time");
        assertTrue(result.contains("Final:5.00"), "Should contain final steady-state value");
    }
}