package com.rikkamus.craftersoneclaimvisualizer.render;

public enum Alignment {

    START {

        @Override
        public int translate(int pos, int size) {
            return pos;
        }

    },
    CENTER {

        @Override
        public int translate(int pos, int size) {
            return pos - size / 2;
        }

    },
    END {

        @Override
        public int translate(int pos, int size) {
            return pos - size;
        }

    };

    public abstract int translate(int pos, int size);

}
