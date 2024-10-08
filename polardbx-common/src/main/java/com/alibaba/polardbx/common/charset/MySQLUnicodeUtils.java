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

package com.alibaba.polardbx.common.charset;

import io.airlift.slice.DynamicSliceOutput;
import io.airlift.slice.Slice;
import io.airlift.slice.SliceOutput;

public class MySQLUnicodeUtils {
    public static byte[][] LATIN1_TO_UTF8_BYTES = {
        {(byte) 0x00}, {(byte) 0x01}, {(byte) 0x02}, {(byte) 0x03}, {(byte) 0x04}, {(byte) 0x05}, {(byte) 0x06},
        {(byte) 0x07}, {(byte) 0x08}, {(byte) 0x09}, {(byte) 0x0A}, {(byte) 0x0B}, {(byte) 0x0C}, {(byte) 0x0D},
        {(byte) 0x0E}, {(byte) 0x0F}, {(byte) 0x10}, {(byte) 0x11}, {(byte) 0x12}, {(byte) 0x13}, {(byte) 0x14},
        {(byte) 0x15}, {(byte) 0x16}, {(byte) 0x17}, {(byte) 0x18}, {(byte) 0x19}, {(byte) 0x1A}, {(byte) 0x1B},
        {(byte) 0x1C}, {(byte) 0x1D}, {(byte) 0x1E}, {(byte) 0x1F}, {(byte) 0x20}, {(byte) 0x21}, {(byte) 0x22},
        {(byte) 0x23}, {(byte) 0x24}, {(byte) 0x25}, {(byte) 0x26}, {(byte) 0x27}, {(byte) 0x28}, {(byte) 0x29},
        {(byte) 0x2A}, {(byte) 0x2B}, {(byte) 0x2C}, {(byte) 0x2D}, {(byte) 0x2E}, {(byte) 0x2F}, {(byte) 0x30},
        {(byte) 0x31}, {(byte) 0x32}, {(byte) 0x33}, {(byte) 0x34}, {(byte) 0x35}, {(byte) 0x36}, {(byte) 0x37},
        {(byte) 0x38}, {(byte) 0x39}, {(byte) 0x3A}, {(byte) 0x3B}, {(byte) 0x3C}, {(byte) 0x3D}, {(byte) 0x3E},
        {(byte) 0x3F}, {(byte) 0x40}, {(byte) 0x41}, {(byte) 0x42}, {(byte) 0x43}, {(byte) 0x44}, {(byte) 0x45},
        {(byte) 0x46}, {(byte) 0x47}, {(byte) 0x48}, {(byte) 0x49}, {(byte) 0x4A}, {(byte) 0x4B}, {(byte) 0x4C},
        {(byte) 0x4D}, {(byte) 0x4E}, {(byte) 0x4F}, {(byte) 0x50}, {(byte) 0x51}, {(byte) 0x52}, {(byte) 0x53},
        {(byte) 0x54}, {(byte) 0x55}, {(byte) 0x56}, {(byte) 0x57}, {(byte) 0x58}, {(byte) 0x59}, {(byte) 0x5A},
        {(byte) 0x5B}, {(byte) 0x5C}, {(byte) 0x5D}, {(byte) 0x5E}, {(byte) 0x5F}, {(byte) 0x60}, {(byte) 0x61},
        {(byte) 0x62}, {(byte) 0x63}, {(byte) 0x64}, {(byte) 0x65}, {(byte) 0x66}, {(byte) 0x67}, {(byte) 0x68},
        {(byte) 0x69}, {(byte) 0x6A}, {(byte) 0x6B}, {(byte) 0x6C}, {(byte) 0x6D}, {(byte) 0x6E}, {(byte) 0x6F},
        {(byte) 0x70}, {(byte) 0x71}, {(byte) 0x72}, {(byte) 0x73}, {(byte) 0x74}, {(byte) 0x75}, {(byte) 0x76},
        {(byte) 0x77}, {(byte) 0x78}, {(byte) 0x79}, {(byte) 0x7A}, {(byte) 0x7B}, {(byte) 0x7C}, {(byte) 0x7D},
        {(byte) 0x7E}, {(byte) 0x7F},

        {(byte) 0xC2, (byte) 0x80}, {(byte) 0xC2, (byte) 0x81}, {(byte) 0xC2, (byte) 0x82}, {(byte) 0xC2, (byte) 0x83},
        {(byte) 0xC2, (byte) 0x84},
        {(byte) 0xC2, (byte) 0x85}, {(byte) 0xC2, (byte) 0x86}, {(byte) 0xC2, (byte) 0x87}, {(byte) 0xC2, (byte) 0x88},
        {(byte) 0xC2, (byte) 0x89}, {(byte) 0xC2, (byte) 0x8A}, {(byte) 0xC2, (byte) 0x8B},
        {(byte) 0xC2, (byte) 0x8C}, {(byte) 0xC2, (byte) 0x8D}, {(byte) 0xC2, (byte) 0x8E}, {(byte) 0xC2, (byte) 0x8F},
        {(byte) 0xC2, (byte) 0x90}, {(byte) 0xC2, (byte) 0x91}, {(byte) 0xC2, (byte) 0x92},
        {(byte) 0xC2, (byte) 0x93}, {(byte) 0xC2, (byte) 0x94}, {(byte) 0xC2, (byte) 0x95}, {(byte) 0xC2, (byte) 0x96},
        {(byte) 0xC2, (byte) 0x97}, {(byte) 0xC2, (byte) 0x98}, {(byte) 0xC2, (byte) 0x99},
        {(byte) 0xC2, (byte) 0x9A}, {(byte) 0xC2, (byte) 0x9B}, {(byte) 0xC2, (byte) 0x9C}, {(byte) 0xC2, (byte) 0x9D},
        {(byte) 0xC2, (byte) 0x9E}, {(byte) 0xC2, (byte) 0x9F}, {(byte) 0xC2, (byte) 0xA0},
        {(byte) 0xC2, (byte) 0xA1}, {(byte) 0xC2, (byte) 0xA2}, {(byte) 0xC2, (byte) 0xA3}, {(byte) 0xC2, (byte) 0xA4},
        {(byte) 0xC2, (byte) 0xA5}, {(byte) 0xC2, (byte) 0xA6}, {(byte) 0xC2, (byte) 0xA7},
        {(byte) 0xC2, (byte) 0xA8}, {(byte) 0xC2, (byte) 0xA9}, {(byte) 0xC2, (byte) 0xAA}, {(byte) 0xC2, (byte) 0xAB},
        {(byte) 0xC2, (byte) 0xAC}, {(byte) 0xC2, (byte) 0xAD}, {(byte) 0xC2, (byte) 0xAE},
        {(byte) 0xC2, (byte) 0xAF}, {(byte) 0xC2, (byte) 0xB0}, {(byte) 0xC2, (byte) 0xB1}, {(byte) 0xC2, (byte) 0xB2},
        {(byte) 0xC2, (byte) 0xB3}, {(byte) 0xC2, (byte) 0xB4}, {(byte) 0xC2, (byte) 0xB5},
        {(byte) 0xC2, (byte) 0xB6}, {(byte) 0xC2, (byte) 0xB7}, {(byte) 0xC2, (byte) 0xB8}, {(byte) 0xC2, (byte) 0xB9},
        {(byte) 0xC2, (byte) 0xBA}, {(byte) 0xC2, (byte) 0xBB}, {(byte) 0xC2, (byte) 0xBC},
        {(byte) 0xC2, (byte) 0xBD}, {(byte) 0xC2, (byte) 0xBE}, {(byte) 0xC2, (byte) 0xBF},

        {(byte) 0xC3, (byte) 0x80},
        {(byte) 0xC3, (byte) 0x81}, {(byte) 0xC3, (byte) 0x82}, {(byte) 0xC3, (byte) 0x83},
        {(byte) 0xC3, (byte) 0x84}, {(byte) 0xC3, (byte) 0x85}, {(byte) 0xC3, (byte) 0x86}, {(byte) 0xC3, (byte) 0x87},
        {(byte) 0xC3, (byte) 0x88}, {(byte) 0xC3, (byte) 0x89}, {(byte) 0xC3, (byte) 0x8A},
        {(byte) 0xC3, (byte) 0x8B}, {(byte) 0xC3, (byte) 0x8C}, {(byte) 0xC3, (byte) 0x8D}, {(byte) 0xC3, (byte) 0x8E},
        {(byte) 0xC3, (byte) 0x8F}, {(byte) 0xC3, (byte) 0x90}, {(byte) 0xC3, (byte) 0x91},
        {(byte) 0xC3, (byte) 0x92}, {(byte) 0xC3, (byte) 0x93}, {(byte) 0xC3, (byte) 0x94}, {(byte) 0xC3, (byte) 0x95},
        {(byte) 0xC3, (byte) 0x96}, {(byte) 0xC3, (byte) 0x97}, {(byte) 0xC3, (byte) 0x98},
        {(byte) 0xC3, (byte) 0x99}, {(byte) 0xC3, (byte) 0x9A}, {(byte) 0xC3, (byte) 0x9B}, {(byte) 0xC3, (byte) 0x9C},
        {(byte) 0xC3, (byte) 0x9D}, {(byte) 0xC3, (byte) 0x9E}, {(byte) 0xC3, (byte) 0x9F},
        {(byte) 0xC3, (byte) 0xA0}, {(byte) 0xC3, (byte) 0xA1}, {(byte) 0xC3, (byte) 0xA2}, {(byte) 0xC3, (byte) 0xA3},
        {(byte) 0xC3, (byte) 0xA4}, {(byte) 0xC3, (byte) 0xA5}, {(byte) 0xC3, (byte) 0xA6},
        {(byte) 0xC3, (byte) 0xA7}, {(byte) 0xC3, (byte) 0xA8}, {(byte) 0xC3, (byte) 0xA9}, {(byte) 0xC3, (byte) 0xAA},
        {(byte) 0xC3, (byte) 0xAB}, {(byte) 0xC3, (byte) 0xAC}, {(byte) 0xC3, (byte) 0xAD},
        {(byte) 0xC3, (byte) 0xAE}, {(byte) 0xC3, (byte) 0xAF}, {(byte) 0xC3, (byte) 0xB0}, {(byte) 0xC3, (byte) 0xB1},
        {(byte) 0xC3, (byte) 0xB2}, {(byte) 0xC3, (byte) 0xB3}, {(byte) 0xC3, (byte) 0xB4},
        {(byte) 0xC3, (byte) 0xB5}, {(byte) 0xC3, (byte) 0xB6}, {(byte) 0xC3, (byte) 0xB7}, {(byte) 0xC3, (byte) 0xB8},
        {(byte) 0xC3, (byte) 0xB9}, {(byte) 0xC3, (byte) 0xBA}, {(byte) 0xC3, (byte) 0xBB},
        {(byte) 0xC3, (byte) 0xBC}, {(byte) 0xC3, (byte) 0xBD}, {(byte) 0xC3, (byte) 0xBE}, {(byte) 0xC3, (byte) 0xBF}};

    public static Slice latin1ToUtf8(byte[] latin1Bytes) {
        SliceOutput sliceOutput = new DynamicSliceOutput(latin1Bytes.length * 2);
        for (int i = 0; i < latin1Bytes.length; i++) {
            sliceOutput.writeBytes(LATIN1_TO_UTF8_BYTES[((int) latin1Bytes[i]) & 0xFF]);
        }
        return sliceOutput.slice();
    }

    public static boolean utf8ToLatin1(byte[] buff, int begin, int end, byte[] res) {
        int pos = 0;
        boolean isUtf8FromLatin1 = true;
        while (begin < end && pos < res.length) {
            int uc1 = ((int) buff[begin++]) & 0xFF;
            // 0xxxxxxx
            if (uc1 < 0x80) {
                res[pos++] = (byte) uc1;
            } else if (begin < end) {
                if (uc1 != 0xC2 && uc1 != 0xC3) {
                    isUtf8FromLatin1 = false;
                }
                int uc2 = ((int) buff[begin++]) & 0xFF;
                res[pos++] = (byte) (((uc1 & 0x1f) << 6) | (uc2 ^ 0x80));
            } else {
                res[pos++] = (byte) 0xFF;
            }
        }
        return isUtf8FromLatin1;
    }

    public static boolean utf8ToLatin1(Slice slice, byte[] res, int len) {
        int pos = 0, begin = 0, end = slice.length();
        boolean isUtf8FromLatin1 = true;
        while (begin < end && pos < len) {
            int uc1 = ((int) slice.getByte(begin++)) & 0xFF;
            // 0xxxxxxx
            if (uc1 < 0x80) {
                res[pos++] = (byte) uc1;
            } else if (begin < end) {
                if (uc1 != 0xC2 && uc1 != 0xC3) {
                    isUtf8FromLatin1 = false;
                }
                int uc2 = ((int) slice.getByte(begin++)) & 0xFF;
                res[pos++] = (byte) (((uc1 & 0x1f) << 6) | (uc2 ^ 0x80));
            } else {
                res[pos++] = (byte) 0xFF;
            }
        }
        return isUtf8FromLatin1;
    }
}
