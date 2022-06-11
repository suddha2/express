package lk.express.auth.jaxws;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import lk.express.Context;
import lk.express.auth.AuthenticationException;
import lk.express.auth.AuthenticationHandler;
import lk.express.auth.AuthorizationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Add @HandlerChain( file="handler_chains.xml" ) anotation to the WebService
 * class and place the "handler_chains.xml" file in the same source folder as
 * the WebService class.
 * 
 * handler_chains.xml
 * 
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;jws:handler-chains xmlns:jws="http://java.sun.com/xml/ns/javaee"&gt;
 *   &lt;jws:handler-chain&gt;
 *     &lt;jws:handler&gt;
 *       &lt;jws:handler-class&gt;lk.express.security.jaxws.SOAPAuthenticationHandler&lt;/jws:handler-class&gt;
 *     &lt;/jws:handler&gt;
 *   &lt;/jws:handler-chain&gt;
 * &lt;/jws:handler-chains&gt;
 * </pre>
 */
public class SOAPAuthenticationHandler extends AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

	private static final Logger logger = LoggerFactory.getLogger(SOAPAuthenticationHandler.class);

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		if ((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY)) {
			return true;
		} else {
			return handleInboundMessage(context);
		}
	}

	private boolean handleInboundMessage(SOAPMessageContext context) {

		try {
			String username = null, token = null;

			SOAPMessage soapMsg = context.getMessage();
			SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
			SOAPHeader soapHeader = soapEnv.getHeader();

			@SuppressWarnings("unchecked")
			Iterator<Node> it = soapHeader.extractAllHeaderElements();
			while (it.hasNext()) {
				Node node = it.next();
				if (node != null) {
					if (USERNAME.equals(node.getLocalName())) {
						username = node.getValue();
					} else if (TOKEN.equals(node.getLocalName())) {
						token = node.getValue();
					}
				}
			}

			String user = authenticate(username, token);
			try {
				SOAPMessage message = context.getMessage();
				String methodName = message.getSOAPBody().getFirstChild().getLocalName();
				if (authorize(user, methodName)) {
					Context.getSessionData().empty().setUsername(user);
					logger.info("WebService method invocation, user: {}, method: {}", new Object[] { username,
							methodName });
				} else {
					String u = user == null ? "Anonymous user" : username;
					logger.info("Authorization failed for user: {} for method: {}", new Object[] { u, methodName });
					throw new AuthorizationException("Authorization failed for user: " + u + " for method: "
							+ methodName);
				}
			} catch (SOAPException e) {
				logger.error("Exception while authorizing", e);
				throw new AuthorizationException("Internal error while authorizing");
			}
		} catch (AuthorizationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Exception while authenticating", e);
			throw new AuthenticationException("Internal error while authenticating");
		}

		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {

		try {
			String username = null, methodName = null;

			SOAPMessage soapMsg = context.getMessage();
			SOAPEnvelope soapEnv = soapMsg.getSOAPPart().getEnvelope();
			SOAPHeader soapHeader = soapEnv.getHeader();

			@SuppressWarnings("unchecked")
			Iterator<Node> it = soapHeader.extractAllHeaderElements();
			while (it.hasNext()) {
				Node node = it.next();
				if (node != null) {
					if (USERNAME.equals(node.getLocalName())) {
						username = node.getValue();
					}
				}
			}
			SOAPMessage message = context.getMessage();
			methodName = message.getSOAPBody().getFirstChild().getLocalName();
			logger.info("WebService fault, user: {}, method: {}", new Object[] { username, methodName });

		} catch (SOAPException e) {
			logger.error("Exception in SOAPAuthHandler", e);
		}

		return true;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
}
