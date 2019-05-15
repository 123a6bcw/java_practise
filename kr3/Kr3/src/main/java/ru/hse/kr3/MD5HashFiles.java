package ru.hse.kr3;


import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Class for md5 hashing files summ in directory or file using one thread and fork-join guy.
 */
public class MD5HashFiles {
    public static void main(String[] argc) throws IOException, ExecutionException, InterruptedException {
        if (argc.length == 0) {
            System.out.println("Daun vvedi parametri");
            return;
        }

        String filename = argc[0];

        File file = new File(filename);

        if (file.isDirectory() || file.isFile()) {
            long startTimeOdno = System.currentTimeMillis();
            String resultOdn = ByteToString(runOdnopotochno(file));

            long resultTimeOdno = System.currentTimeMillis() - startTimeOdno;

            long startTimeMnogo = System.currentTimeMillis();
            String resultMnogo = ByteToString(runMnogoPotochno(file));
            long resultTimeMnogo = System.currentTimeMillis() - startTimeMnogo;

            System.out.println(resultOdn);
            System.out.println(resultMnogo);
            System.out.println(resultTimeOdno);
            System.out.println(resultTimeMnogo);
        } else {
            System.out.println("Daun vvedi norm imya faila");
            return;
        }
    }

    /**
     * Converts byte array to it's hex string representation.
     */
    @NotNull
    private static String ByteToString(@NotNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        // convert the byte to hex format
        for (byte aByte : bytes) {
            String s = Integer.toHexString(0xff & aByte);
            s = (s.length() == 1) ? "0" + s : s;
            sb.append(s);
        }

        return sb.toString();
    }

    /**
     * Runs md5 hashing of file/directory using single thread.
     * If file is directory, result MD5 is MD5(<имя папки> + f(file1) + ...)
     * otherwise it's MD5(<содержимое>)
     */
    @NotNull
    private static byte[] runOdnopotochno(@NotNull File file) throws IOException {
        MessageDigest digest = getMessageDigest();

        if (file.isDirectory()) {
            digest.update(file.getName().getBytes());

            var fileList = file.listFiles();

            if (fileList != null) {
                for (var subfile : fileList) {
                    digest.update(runOdnopotochno(subfile));
                }
            }

            return digest.digest();
        } else {
            try (var inputStream = new FileInputStream(file);
                    var digestInputStream = new DigestInputStream(inputStream, digest)) {

                //noinspection StatementWithEmptyBody
                while (digestInputStream.read() != -1) {
                }

                return digest.digest();
            }
        }
    }

    /**
     * Creates new instances of message digest and prints something offensive if programmer is too stupid to write
     * word MD5 correctly.
     */
    @NotNull
    private static MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Lol daun napishi MD5 normalno");
            throw new RuntimeException("Whatever it will never happen");
        }
    }

    /**
     * Runs md5 hashing of directory using fork-join-poll.
     * If file is directory, result MD5 is MD5(<имя папки> + f(file1) + ...)
     * otherwise it's MD5(<содержимое>)
     */
    @NotNull
    private static byte[] runMnogoPotochno(@NotNull File file) throws IOException, ExecutionException, InterruptedException {
        var forkJoinPull = new ForkJoinPool();
        var task = forkJoinPull.submit(new MnogoPotokMd5(file));

        var result = task.get();
        if (result == null) {
            throw new IOException("Something TERRIBLE happened during the read :(((");
        } else {
            return result;
        }
    }

    /**
     * Recursive task for fork-join-pool.
     */
    private static class MnogoPotokMd5 extends RecursiveTask<byte[]> {
        private final File file;
        private final String fileName;

        private MnogoPotokMd5(File file) {
            this.file = file;
            fileName = null;
        }

        private MnogoPotokMd5(String fileName) {
            file = null;
            this.fileName = fileName;
        }

        @Override
        @Nullable
        public byte[] compute() {
            MessageDigest digest = getMessageDigest();

            if (fileName != null) {
                return digest.digest(fileName.getBytes());
            } else
            if (Objects.requireNonNull(file).isDirectory()) { //File is always not null if fileName is Null
                var task = new MnogoPotokMd5(file.getName());
                task.fork();

                var fileList = file.listFiles();
                if (fileList == null) {
                    fileList = new File[0];
                }

                var stream = Arrays.stream(fileList).map(MnogoPotokMd5::new).peek(ForkJoinTask::fork);
                digest.update(task.join());
                stream.forEach(mnogoPotokMd5 -> digest.update(mnogoPotokMd5.join()));

                return digest.digest();
            } else {
                try (var inputStream = new FileInputStream(file);
                     var digestInputStream = new DigestInputStream(inputStream, digest)) {

                    //noinspection StatementWithEmptyBody
                    while (digestInputStream.read() != -1) {
                    }

                    return digest.digest();
                } catch (IOException e) {
                    return null;
                }
            }
        }
    }
}
