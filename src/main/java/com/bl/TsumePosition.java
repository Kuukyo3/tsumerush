package com.bl;

public class TsumePosition {
    private String sfen;
    private String[] solutionMoves;

    public TsumePosition(String sfen, String[] solutionMoves) {
        this.sfen = sfen;
        this.solutionMoves = solutionMoves;
    }

    public String getSfen() {
        return sfen;
    }

    public void setSfen(String sfen) {
        this.sfen = sfen;
    }

    public String[] getSolutionMoves() {
        return solutionMoves;
    }

    public void setSolutionMoves(String[] solutionMoves) {
        this.solutionMoves = solutionMoves;
    }

    
}