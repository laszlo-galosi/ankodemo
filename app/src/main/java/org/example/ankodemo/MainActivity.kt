package org.example.ankodemo

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import org.example.ankodemo.util.Validations
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.activityUiThreadWithContext
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.button
import org.jetbrains.anko.dip
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.verticalLayout
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
                if (checkCredentials(name.toString(), password.toString())) {
                    toast("Logged in! :)")
                    startActivity<CountriesActivity>()
                } else {
                    toast("Wrong password :( Enter user:password")
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

//            val email = inflatedAnkoView(R.layout.widget_edit_field_w_shadow, {
            email = inflatedAnkoView(R.layout.widget_edit_field, {
                id = R.id.field_email
                with(viewBinding) {
                    setVariable(BR.fieldLabel, resources.getString(R.string.email))
                    setVariable(BR.fieldHint, resources.getString(R.string.email))
                    setVariable(BR.fieldInputType,
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    setVariable(BR.fieldValue, "laszlo.galosi@gmail.com")
                }
            })

            password = inflatedAnkoView(R.layout.widget_edit_field, {
                id = R.id.field_password
                with(viewBinding) {
                    setVariable(BR.fieldLabel, resources.getString(R.string.password))
                    setVariable(BR.fieldHint, resources.getString(R.string.password))
                    setVariable(BR.fieldInputType,
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    setVariable(BR.fieldPasswordToggle, true)
                }
            })

            /*val passwordField = customInputField {
                label = resources.getString(R.string.password)
                hintText = resources.getString(R.string.password)
                inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                imeAction = EditorInfo.IME_ACTION_DONE
                imeLabel = resources.getString(R.string.action_done)
                onEditorAction = TextView.OnEditorActionListener { _, action, _ ->
                    when (action) {
                        EditorInfo.IME_ACTION_DONE -> {
                            ui.owner.tryLogin(ui, name.value, this.value)
                            true
                        }
                        else -> false
                    }
                }
            }*/

            button("Log in") {
                onClick {
                    if (validations(ui.ctx).result) {
                        val passwValue = password.find<EditText>(R.id.editField)?.text.toString()
                        ui.owner.tryLogin(ui, name.value, passwValue)
                    }
                }
            }
        }.applyRecursively(customStyle)
    }

    fun validations(context: Context): Validations {
        return Validations.with(context).callback(this).apply {
            val passwValue = password.find<EditText>(R.id.editField)?.text.toString()
            val emailValue = email.find<EditText>(R.id.editField)?.text.toString()

            if (resultTag(name).isNotEmpty(name.value).result) {
                equalTo(name.value as String, "user", "Wrong user :( Enter user:user")
            }
            if (resultTag(email).isNotEmpty(emailValue).result) {
                isValidEmail(emailValue)
            }
            if (resultTag(password).isNotEmpty(passwValue).result
                    and isValidPassword(passwValue).result) {
                equalTo(passwValue, "password", "Wrong password :( Enter user:password")
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
            tag.errorText = error
            tag.bind()
        } else {
            tag.find<TextInputLayout>(R.id.inputLayout)?.error = error
        }
    }
}
