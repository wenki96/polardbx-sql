/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.polardbx.server.parser;

import com.alibaba.polardbx.druid.sql.parser.ByteString;
import com.alibaba.polardbx.server.util.ParseUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author xianmao.hexm 2011-5-7 下午01:23:06
 */
public final class ServerParseClear {

    public static final int OTHER = -1;
    public static final int SLOW = 1;
    public static final int PLANCACHE = 2;
    public static final int OSSCACHE = 3;

    public static final int HEATMAP_CACHE = 4;
    public static final int PROCEDURE_CACHE = 5;
    public static final int FUNCTION_CACHE = 6;

    public static final int ALLCACHE = 7;
    public static final int EXTERNAL_DISK_CACHE = 8;
    public static final int NFS_CACHE = 9;
    public static final int S3_CACHE = 10;
    public static final int ABS_CACHE = 11;

    public static final Set<Integer> PREPARE_UNSUPPORTED_CLEAR_TYPE;

    static {
        PREPARE_UNSUPPORTED_CLEAR_TYPE = new HashSet<>();

        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(SLOW);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(PLANCACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(OSSCACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(HEATMAP_CACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(PROCEDURE_CACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(FUNCTION_CACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(ALLCACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(EXTERNAL_DISK_CACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(NFS_CACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(S3_CACHE);
        PREPARE_UNSUPPORTED_CLEAR_TYPE.add(ABS_CACHE);
    }

    public static int parse(ByteString stmt, int offset) {
        int i = offset;
        for (; i < stmt.length(); i++) {
            switch (stmt.charAt(i)) {
            case ' ':
                continue;
            case '/':
            case '#':
                i = ParseUtil.comment(stmt, i);
                continue;
            case 'S':
            case 's':
                if (i + 1 < stmt.length() && Character.toLowerCase(stmt.charAt(i + 1)) == 'l') {
                    return slowCheck(stmt, i);
                } else {
                    return s3CacheCheck(stmt, i);
                }
            case 'P':
            case 'p':
                return pCheck(stmt, i);
            case 'O':
            case 'o':
                return ossCacheCheck(stmt, i);
            case 'A':
            case 'a':
                if (i + 1 < stmt.length() && Character.toLowerCase(stmt.charAt(i + 1)) == 'l') {
                    return allCacheCheck(stmt, i);
                } else {
                    return absCacheCheck(stmt, i);
                }
            case 'N':
            case 'n':
                return nfsCacheCheck(stmt, i);
            case 'E':
            case 'e':
                return externalDiskCacheCheck(stmt, i);
            case 'H':
            case 'h':
                return partitionsHeatmapCacheCheck(stmt, i);
            case 'F':
            case 'f':
                return functionCheck(stmt, i);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    private static int functionCheck(ByteString stmt, int offset) {
        final String expect = "FUNCTION CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return FUNCTION_CACHE;
            }
        }
        return OTHER;
    }

    // CLEAR SLOW
    private static int slowCheck(ByteString stmt, int offset) {
        if (stmt.length() > offset + "low".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'L' || c1 == 'l') && (c2 == 'O' || c2 == 'o') && (c3 == 'W' || c3 == 'w')
                && (stmt.length() == ++offset || ParseUtil.isEOF(stmt.charAt(offset)))) {
                return SLOW;
            }
        }
        return OTHER;
    }

    private static int pCheck(ByteString stmt, int offset) {
        final String expect = "PROCEDURE CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return PROCEDURE_CACHE;
            }
        }
        return planCacheCheck(stmt, offset);
    }

    // CLEAR PLANCACHE
    private static int planCacheCheck(ByteString stmt, int offset) {
        final String expect = "PLANCACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return PLANCACHE;
            }
        }
        return OTHER;
    }

    // CLEAR OssCache
    private static int ossCacheCheck(ByteString stmt, int offset) {
        final String expect = "OSS CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return OSSCACHE;
            }
        }
        return OTHER;
    }

    // CLEAR NfsCache
    private static int nfsCacheCheck(ByteString stmt, int offset) {
        final String expect = "NFS CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return NFS_CACHE;
            }
        }
        return OTHER;
    }

    // CLEAR ExternalDiskCache
    private static int externalDiskCacheCheck(ByteString stmt, int offset) {
        final String expect = "EXTERNAL_DISK CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return EXTERNAL_DISK_CACHE;
            }
        }
        return OTHER;
    }

    // CLEAR S3Cache
    private static int s3CacheCheck(ByteString stmt, int offset) {
        final String expect = "S3 CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return S3_CACHE;
            }
        }
        return OTHER;
    }

    // CLEAR ABSCache
    private static int absCacheCheck(ByteString stmt, int offset) {
        final String expect = "ABS CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return ABS_CACHE;
            }
        }
        return OTHER;
    }

    private static int allCacheCheck(ByteString stmt, int offset) {
        final String expect = "ALL CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return ALLCACHE;
            }
        }
        return OTHER;
    }

    private static int partitionsHeatmapCacheCheck(ByteString stmt, int offset) {
        final String expect = "HEATMAP_CACHE";
        if (stmt.length() >= offset + expect.length()) {
            if (stmt.substring(offset, offset + expect.length()).equalsIgnoreCase(expect)) {
                return HEATMAP_CACHE;
            }
        }
        return OTHER;
    }
}
