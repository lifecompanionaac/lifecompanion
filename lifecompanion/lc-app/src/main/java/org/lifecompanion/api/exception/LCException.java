/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.api.exception;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Exception thrown when the applicaton logic is not respected.<br>
 * This exception is use show "normal" problems to user.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String messageId;
    private final String headerId;
    private final String header;
    private final String message;

    private LCException(Throwable cause, String messageId, String headerId, String header, String message) {
        super(cause);
        this.messageId = messageId;
        this.headerId = headerId;
        this.header = header;
        this.message = message;
    }

    public String getUserMessage() {
        return this.message != null ? this.message : Translation.getText(this.messageId);
    }

    public String getUserHeader() {
        return this.header != null ? this.header : this.headerId != null ? Translation.getText(this.headerId) : null;
    }

    public static LCExceptionBuilder newException() {
        return new LCExceptionBuilder();
    }

    public static class LCExceptionBuilder {
        private Throwable cause;
        private String messageId, message;
        private String headerId, header;

        private LCExceptionBuilder() {
        }

        public LCExceptionBuilder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }

        public LCExceptionBuilder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public LCExceptionBuilder withHeaderId(String headerId) {
            this.headerId = headerId;
            return this;
        }

        public LCExceptionBuilder withMessage(String messageId, Object... args) {
            this.message = Translation.getText(messageId, args);
            return this;
        }

        public LCExceptionBuilder withHeader(String headerId, Object... args) {
            this.header = Translation.getText(headerId, args);
            return this;
        }

        public LCException build() {
            return new LCException(cause, messageId, headerId, header, message);
        }

        public void buildAndThrow() throws LCException {
            throw build();
        }
    }
}
