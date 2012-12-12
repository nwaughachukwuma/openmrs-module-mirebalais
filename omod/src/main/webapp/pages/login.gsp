<!-- This is the login page -->
<%
    ui.includeFragment("emr", "standardEmrIncludes")

    ui.setPageTitle(ui.message("mirebalais.login.welcomeHeading"))

    ui.includeCss("mirebalais", "login.css")
%>

<div id="body-wrapper" class="container">
    <div id="content">

        <img id="pih-logo" src="${ ui.resourceLink("mirebalais", "images/PIH_ZL_plum_940.jpg") }"/>

        <h1>${ ui.message("mirebalais.login.welcomeHeading") }</h1>

        <form id="login-form" method="post" autocomplete="off">
            <fieldset>

                <legend>
                    <i class="icon-lock small"></i>
                    ${ ui.message("mirebalais.login.loginHeading") }
                </legend>

                ${ ui.includeFragment("emr", "infoAndErrorMessage") }

                <p class="left">
                    <label for="username">
                        ${ ui.message("mirebalais.login.username") }:
                    </label>
                    <input id="username" type="text" name="username" placeholder="Enter your username"/>
                </p>

                <p class="left">
                    <label for="password">
                        ${ ui.message("mirebalais.login.password") }:
                    </label>
                    <input id="password" type="password" name="password" placeholder="Enter your password"/>
                </p>

                <p class="clear">
                    <label for="sessionLocation">
                        ${ ui.message("mirebalais.login.sessionLocation") }:
                    </label>
                    <ul id="sessionLocation" class="select">
                        <% locations.each { %>
                            <li value="${it.id}">${ui.format(it)}</li>
                        <% } %>
                    </ul>
                </p>

                <input type="hidden" id="sessionLocationInput" name="sessionLocation"
                       <% if (lastSessionLocation != null) { %> value="${lastSessionLocation.id}" <% } %> />

                <p>
                    <input id="login-button" class="confirm" type="submit" value="${ ui.message("mirebalais.login.button") }"/>
                </p>
                <p>
                    <a id="cant-login" href="javascript:void(0)">
                        <i class="icon-question-sign small"></i>
                        ${ ui.message("mirebalais.login.cannotLogin") }
                    </a>
                </p>

            </fieldset>

        </form>

    </div>
</div>
<div id="cannot-login-popup">
    ${ ui.message("mirebalais.login.cannotLoginInstructions") }
</div>

<script type="text/javascript">
	document.getElementById('username').focus();
    updateSelectedOption = function() {
        \$('#sessionLocation li').removeClass('selected');
        \$('#sessionLocation li[value|=' + \$('#sessionLocationInput').val() + ']').addClass('selected');
    };

    \$(function() {
        updateSelectedOption();

        \$('#sessionLocation li').click( function() {
            \$('#sessionLocationInput').val(\$(this).attr("value"));
            updateSelectedOption();
        });

        \$('#cannot-login-popup').dialog({
            autoOpen: false,
            modal: true,
            title: "${ ui.message("mirebalais.login.cannotLogin") }",
            width: 750,
            height: 200
        });
        \$('a#cant-login').click(function() {
            \$('#cannot-login-popup').dialog('open');
        })
    });
</script>