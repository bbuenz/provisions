/*
 * Decompiled with CFR 0_110.
 */
package edu.stanford.crypto;

import java.sql.SQLException;

public interface SQLDatabase
extends AutoCloseable {
    @Override
    void close() throws SQLException;
}

