package com.common.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellUtils {
    private static final String LINE_SEP = System.getProperty("line.separator");

    public static CommandResult execCmd(String[] commands,
                                        boolean isRooted,
                                        boolean isNeedResultMsg) {
        int result = -1;
        if (commands != null && commands.length != 0) {
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            DataOutputStream os = null;

            try {
                process = Runtime.getRuntime().exec(isRooted ? "su" : "sh");
                os = new DataOutputStream(process.getOutputStream());
                String[] var10 = commands;
                int var11 = commands.length;

                for (int var12 = 0; var12 < var11; ++var12) {
                    String command = var10[var12];
                    if (command != null) {
                        os.write(command.getBytes());
                        os.writeBytes(LINE_SEP);
                        os.flush();
                    }
                }

                os.writeBytes("exit" + LINE_SEP);
                os.flush();
                result = process.waitFor();
                if (isNeedResultMsg) {
                    successMsg = new StringBuilder();
                    errorMsg = new StringBuilder();
                    successResult =
                            new BufferedReader(new InputStreamReader(process.getInputStream(),
                                    "UTF-8"));
                    errorResult =
                            new BufferedReader(new InputStreamReader(process.getErrorStream(),
                                    "UTF-8"));
                    String line;
                    if ((line = successResult.readLine()) != null) {
                        successMsg.append(line);

                        while ((line = successResult.readLine()) != null) {
                            successMsg.append(LINE_SEP).append(line);
                        }
                    }

                    if ((line = errorResult.readLine()) != null) {
                        errorMsg.append(line);

                        while ((line = errorResult.readLine()) != null) {
                            errorMsg.append(LINE_SEP).append(line);
                        }
                    }
                }
            } catch (Exception var30) {
                var30.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException var29) {
                    var29.printStackTrace();
                }

                try {
                    if (successResult != null) {
                        successResult.close();
                    }
                } catch (IOException var28) {
                    var28.printStackTrace();
                }

                try {
                    if (errorResult != null) {
                        errorResult.close();
                    }
                } catch (IOException var27) {
                    var27.printStackTrace();
                }

                if (process != null) {
                    process.destroy();
                }

            }

            return new CommandResult(result, successMsg == null ? "" : successMsg.toString(),
                    errorMsg == null
                            ? ""
                            : errorMsg.toString());
        } else {
            return new CommandResult(result, "", "");
        }
    }

    public static class CommandResult {
        public int    result;
        public String successMsg;
        public String errorMsg;

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        public String toString() {
            return "result: " + this.result + "\nsuccessMsg: " + this.successMsg + "\nerrorMsg: " + this.errorMsg;
        }
    }
}
