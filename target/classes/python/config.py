from flask import Flask, redirect, url_for, request, session
app = Flask(__name__, static_url_path = '', static_folder = 'static', template_folder = 'templates')
app.secret_key = "\x18\x13\xcb\xd0\x01=k?\x9b\xb8\x9aF\x81\xe0\xf4\x18L\x9b\xb1\xe7\xe1b\xc4^"
app.config['SEND_FILE_MAX_AGE_DEFAULT'] = 0
app.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://summarization:lattice@localhost:5432'
app.debug = True
if app.debug:
	from flask_debugtoolbar import DebugToolbarExtension
	# app.config['DEBUG_TB_INTERCEPT_REDIRECTS'] = True
	toolbar = DebugToolbarExtension(app)
