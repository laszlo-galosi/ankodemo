package org.example.ankodemo

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.example.ankodemo.ankoviews.InflatedAnkoView
import org.example.ankodemo.ankoviews.inflatedDataBindingAnkoView
import org.example.ankodemo.util.Validations
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.activityUiThreadWithContext
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.button
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.toast
import org.jetbrains.anko.verticalLayout
import org.jetbrains.anko.wrapContent
import trikita.log.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUi().setContentView(this)
    }

    fun tryLogin(ui: AnkoContext<MainActivity>, name: CharSequence?, password: CharSequence?) {
        ui.doAsync {
            Thread.sleep(500)

            activityUiThreadWithContext {
                Log.d("tryLogin", name, password)
                if (checkCredentials(name.toString(), password.toString())) {
                    toast("Logged in! :)")
                    startActivity<CountriesActivity>()
                } else {
                    toast("Wrong password :( Enter:password")
                }
            }
        }
    }

    private fun checkCredentials(name: String,
            password: String) = name == "user" && password == "password"
}

class MainActivityUi : AnkoComponent<MainActivity>, Validations.Callback {
    lateinit var name: InputField
    lateinit var password: InflatedAnkoView
    lateinit var email: InflatedAnkoView

    private val customStyle = { v: Any ->
        when (v) {
            is Button -> v.textSize = 26f
            is EditText -> v.textSize = 24f
        }
    }

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        nestedScrollView {
            lparams(width = matchParent, height = wrapContent)
            verticalLayout {
                padding = dip(32)

                /*imageView(android.R.drawable.ic_menu_manage).lparams {
                    margin = dip(16)
                    gravity = Gravity.CENTER
                }
                val name = editText {
                hintResource = R.string.name
                }
                val password = editText {
                    hintResource = R.string.password
                    inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                }

                textInputLayout {
                    isHintEnabled = true
                    isHintAnimationEnabled = true
                    isPasswordVisibilityToggleEnabled=true
                    textInputEditText {
                        hintResource = R.string.password
                        inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                    }
                }*/

                myRichView()

                name = customInputField {
                    id = R.id.field_name
                    label = resources.getString(R.string.name)
                    hintText = resources.getString(R.string.name)
                }

                email = inflatedDataBindingAnkoView(R.layout.widget_edit_field, {
                    id = R.id.field_email
                    with(viewBinding) {
                        setVariable(BR.fieldLabel, resources.getString(R.string.email))
                        setVariable(BR.fieldHint, resources.getString(R.string.email))
                        setVariable(BR.fieldInputType,
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                        setVariable(BR.fieldValue, "laszlo.galosi78@gmail.com")
                    }
                })

                password = inflatedDataBindingAnkoView(R.layout.widget_edit_field, {
                    id = R.id.field_password
                    with(viewBinding) {
                        setVariable(BR.fieldLabel, resources.getString(R.string.password))
                        setVariable(BR.fieldHint, resources.getString(R.string.password))
                        setVariable(BR.fieldInputType,
                                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        setVariable(BR.fieldPasswordToggle, true)
                        this.root.findViewById<TextInputEditText>(R.id.editField).apply {
                            transformationMethod = PasswordTransformationMethod.getInstance()
                        }
                    }
                })

                button("Log in") {

                    backgroundColor = ctx.colorRes(R.color.material_deep_teal_500)
                    onClick {
                        if (validations(ui.ctx).result) {
                            val passwValue = password.let {
                                it.find<EditText>(R.id.editField).text.toString()
                            }
                            ui.owner.tryLogin(ui, name.value, passwValue)
                        }
                    }
                }.lparams {
                    width = matchParent
                    topMargin = dip(16)
                    bottomMargin = dip(16)
                }
            } //VerticalLayout
        }.applyRecursively(customStyle)
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
                helperTextColor = ContextCompat.getColor(ctx, R.color.error_color_material)
                bind()
            }
        } else {
            tag.find<TextInputLayout>(R.id.inputLayout).error = error
        }
    }
}
