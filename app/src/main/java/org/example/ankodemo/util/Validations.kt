package org.example.ankodemo.util

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.util.Preconditions
import android.text.TextUtils
import android.util.Log
import org.example.ankodemo.R

/**
 * Created by László Gálosi on 06/12/17
 */
class Validations(private val mContext: Context) {
    private var mResultTag: Any? = null
    private var mResult: Boolean? = java.lang.Boolean.TRUE
    private var mCallback: Validations.Callback? = null

    val result: Boolean
        get() {
            checks()
            return mResult!!
        }

    fun resultTag(tag: Any): Validations {
        mResultTag = tag
        return this
    }

    fun callback(cb: Callback): Validations {
        mCallback = cb
        return this
    }

    fun lengthExactly(value: Any, length: Int): Validations {
        checks()
        mResult = mResult?.and(callback((value as String).length == length, value,
                mContext.getString(R.string.error_field_length_equal, length)))
        return this
    }

    @SuppressLint("RestrictedApi")
    private fun checks() {
        Preconditions.checkNotNull(mContext, "Context is null")
        if (mResultTag == null) {
            Log.w(TAG, "No result tag set.")
        }
    }

    private fun callback(result: Boolean, value: Any?, error: String?): Boolean {
        if (mCallback == null) {
            return result
        }
        mCallback!!.onValidated(mResultTag, value, if (result) null else error)
        return result
    }

    fun isValidEmail(value: Any): Validations {
        if (isNotEmpty(value).result) {
            val ePattern = EMAIL_PATTERN
            return matches(value, ePattern, mContext.getString(R.string.error_field_invalid_email))
        }
        return this
    }

    fun isNotEmpty(value: Any?): Validations {
        checks()
        if (value is String) {
            mResult = mResult?.and(callback(!TextUtils.isEmpty(value as CharSequence?), value,
                    mContext.getString(R.string.error_field_required)))
        } else {
            mResult = mResult?.and(callback(value != null, value,
                    mContext.getString(R.string.error_field_required)))
        }
        return this
    }

    fun matches(value: Any, pattern: String, error: String): Validations {
        checks()
        val p = java.util.regex.Pattern.compile(pattern)
        mResult = mResult?.and(callback(p.matcher(value as String).matches(), value, error))
        return this
    }

    fun optional(value: Any): Validations {
        checks()
        mResult = mResult?.and(callback(true, value, null))
        return this
    }

    fun isValidPassword(value: Any): Validations {
        checks()
        return if (isNotEmpty(value).result) {
            lengthGreaterThan(value, 6)
            ///if (lengthGreaterThan(value, 6).getResult()) {
            //String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
            //matches(value, pattern, mContext.getString(R.string.error_field_invalid_password));
            //}
        } else this
    }

    fun lengthGreaterThan(value: Any, min: Int): Validations {
        checks()
        mResult = mResult?.and(callback((value as String).length >= min, value,
                mContext.getString(R.string.error_field_length_minimum, min)))
        return this
    }

    fun isValidPhone(value: Any): Validations {
        checks()
        return if (isNotEmpty(value).result) {
            lengthLessThan(value, 12)
        } else this
    }

    fun lengthLessThan(value: Any, max: Int): Validations {
        checks()
        mResult = mResult?.and(callback((value as String).length <= max, value,
                mContext.getString(R.string.error_field_length_maximum, max)))
        return this
    }

    fun equalTo(value: Any, expected: Any, error: String): Validations {
        checks()
        mResult = mResult?.and(callback(value == expected, value, error))
        return this
    }

    interface Callback {
        /**
         * Callback called when all the validation for the  tag specified field has finished.
         *
         * @param tag identifying the field usually the view itself to display error messages on.
         * @param value the validated field value
         * @param error the error message if the validation failed.
         */
        fun onValidated(tag: Any?, value: Any?, error: String?)
    }

    companion object {
        val TAG = "Validations"
        val EMAIL_PATTERN = (
                "^[a-zóüöúőűáéíA-ZÓÜÖÚŐŰÁÉÍ00-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,"
                        + "3}\\.[0-9]{1,3}\\"
                        + ".[0-9]{1,3}])|(([a-zóüöúőűáéíA-ZÓÜÖÚŐŰÁÉÍ0\\-0-9]+\\.)+[a-zA-Z]{2,})"
                        + ")$")

        fun with(context: Context): Validations = Validations(context)
    }
}
