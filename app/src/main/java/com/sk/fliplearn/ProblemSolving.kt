package com.sk.fliplearn


//
// Created by SK(Sk) on 02/06/20.
// Copyright (c) 2020 Sktech. All rights reserved.

class ProblemSolving {
    fun findNumberOfPath(
        source: IntArray,
        destination: IntArray,
        matrix: Array<IntArray>
    ): Int {

        val m = matrix.size
        val n = matrix[0].size
        val count = Array(m) { IntArray(n) }

        // Count of paths to reach any cell in
        // first column is 1
        for (i in 0 until m) count[i][0] = 1


        // Count of paths to reach any cell in
        // first column is 1
        for (j in 0 until n) count[0][j] = 1


        for (x in source[0]..destination[0]) {
            for (y in source[1]..destination[1]) {

                if (x - 1 >= source[0]) {
                    count[x][y] += count[x - 1][y] // check for left
                }
                if (y - 1 >= source[1]) {
                    count[x][y] += count[x][y - 1] // for right
                }
                if (x - 1 > source[0] && y - 1 >= source[1]) {
                    count[x][y] += count[x - 1][y - 1] // for diagonals

                }
            }
        }
        return count[destination[0]][destination[1]]

    }
}