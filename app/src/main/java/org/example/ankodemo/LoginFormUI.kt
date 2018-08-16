package org.example.ankodemo

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.EditText
import android.widget.LinearLayout
import org.example.ankodemo.R.color
import org.example.ankodemo.R.layout
import org.example.ankodemo.R.string
import org.example.ankodemo.ankoviews.AnkoViewProvider
import org.example.ankodemo.ankoviews.InflatedAnkoView
import org.example.ankodemo.ankoviews.ankoComponent
import org.example.ankodemo.ankoviews.inflatedDataBindingAnkoView
import org.example.ankodemo.util.Validations
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.verticalLayout
import trikita.log.Log

@Suppress("NOTHING_TO_INLINE")
inline fun ViewManager.loginForm(theme: Int = 0, init: LoginFormUI.() -> Unit): View =
        ankoComponent({ LoginFormUI(it) }, theme, init)

class LoginFormUI(context: Context) : LinearLayout(context), AnkoComponent<ViewGroup>,
        AnkoViewProvider, Validations.Callback {

    lateinit var name: InputField
    lateinit var password: InflatedAnkoView
    lateinit var email: InflatedAnkoView
    var cbOnValidated: (String, String) -> Unit = { user, passw ->
        Log.d("onValidated", user, passw)
    }

    override val view: View by lazy {
        createView(AnkoContext.create(context, this as ViewGroup))
    }

    override fun createView(ui: AnkoContext<ViewGroup>): View = with(ui) {
        verticalLayout {
            padding = dip(32)

//            myRichView()

            name = customInputField {
                id = R.id.field_name
                label = resources.getString(string.name)
                hintText = resources.getString(string.name)
            }

            email = inflatedDataBindingAnkoView(layout.widget_edit_field, {
                id = R.id.field_email
                with(viewBinding) {
                    setVariable(BR.fieldLabel, resources.getString(string.email))
                    setVariable(BR.fieldHint, resources.getString(string.email))
                    setVariable(BR.fieldInputType,
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    setVariable(BR.fieldValue, "laszlo.galosi78@gmail.com")
                }
            })

            password = inflatedDataBindingAnkoView(layout.widget_edit_field, {
                id = R.id.field_password
                with(viewBinding) {
                    setVariable(BR.fieldLabel, resources.getString(string.password))
                    setVariable(BR.fieldHint, resources.getString(string.password))
                    setVariable(BR.fieldInputType,
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                    setVariable(BR.fieldPasswordToggle, true)
                    this.root.findViewById<TextInputEditText>(R.id.editField).apply {
                        transformationMethod = PasswordTransformationMethod.getInstance()
                    }
                }
            })

            button("Log in") {
                backgroundColor = ui.ctx.colorRes(color.material_deep_teal_500)
                onClick {
                    if (validations(ui.ctx).result) {
                        val passwValue = password.let {
                            it.find<EditText>(R.id.editField).text.toString()
                        }
                        cbOnValidated.invoke(name.value, passwValue)
                    }
                }
            }.lparams {
                width = matchParent
                topMargin = dip(16)
                bottomMargin = dip(16)
            }
        }
    }

    fun validations(context: Context): Validations {
        return Validations.with(context).callback(this).apply {
            val passwValue = password.let {
                it.find<EditText>(R.id.editField).text.toString()
            }
            val emailValue = email.let {
                it.find<EditText>(R.id.editField).text.toString()
            }

            if (resultTag(name).isNotEmpty(name.value).result) {
                equalTo(name.value, "user", "Wrong user :( Enter: user")
            }
            if (resultTag(email).isNotEmpty(emailValue).result) {
                isValidEmail(emailValue)
            }
            if (resultTag(password).isNotEmpty(passwValue).result
                    and isValidPassword(passwValue).result) {
                equalTo(passwValue, "password", "Wrong password :( Enter: password")
            }
        }
    }

    override fun onValidated(tag: Any?, value: Any?, error: String?) {
        val ctx: Context = (tag as View).context
        val failed = !TextUtils.isEmpty(error)
        if (!failed) {
            Log.d("onValidated", tag.id.resName(ctx), value)
        }
        //Set error text.
        if (tag is InputField) {
            with(tag) {
                errorText = error
                helperTextColor = ContextCompat.getColor(ctx, R.color.field_error_color)
                bind()
            }
        } else {
            tag.find<TextInputLayout>(R.id.inputLayout).apply {
                this.error = error
                this.setErrorTextAppearance(R.style.FieldError)
            }
        }
    }
}
