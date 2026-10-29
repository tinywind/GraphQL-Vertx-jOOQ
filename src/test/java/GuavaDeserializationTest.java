import com.google.common.collect.MapMaker;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GuavaDeserializationTest {
    @Test
    void rejectsAnUntrustedSerializedCapacityWithoutExhaustingTheHeap() throws Exception {
        Process process = new ProcessBuilder(
                Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-Xmx32m", "-cp", System.getProperty("surefire.test.class.path", System.getProperty("java.class.path")),
                Probe.class.getName()).redirectErrorStream(true).start();
        try {
            assertTrue(process.waitFor(15, TimeUnit.SECONDS));
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertEquals(0, process.exitValue(), output);
            assertTrue(output.contains("PASS"), output);
        } finally {
            process.destroyForcibly();
            process.waitFor(5, TimeUnit.SECONDS);
        }
    }

    public static class Probe {
        public static void main(String[] args) throws Exception {
            Map<Object, Object> map = new MapMaker().weakKeys().makeMap();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream stream = new ObjectOutputStream(bytes)) {
                stream.writeObject(map);
            }
            byte[] input = bytes.toByteArray();
            int sizeOffset = -1;
            for (int i = 0; i < input.length - 6; i++) {
                if (input[i] == 0x77 && input[i + 1] == 4 && input[i + 2] == 0 && input[i + 3] == 0
                        && input[i + 4] == 0 && input[i + 5] == 0 && input[i + 6] == 0x70) {
                    sizeOffset = i + 2;
                }
            }
            if (sizeOffset < 0) throw new AssertionError("Missing serialized map size");
            input[sizeOffset] = 0x7f;
            input[sizeOffset + 1] = (byte) 0xff;
            input[sizeOffset + 2] = (byte) 0xff;
            input[sizeOffset + 3] = (byte) 0xff;
            try (ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(input))) {
                Map<?, ?> restored = (Map<?, ?>) stream.readObject();
                if (!restored.isEmpty()) throw new AssertionError("Expected an empty map");
            }
            System.out.println("PASS: serialized capacity does not allocate an oversized map");
        }
    }
}
