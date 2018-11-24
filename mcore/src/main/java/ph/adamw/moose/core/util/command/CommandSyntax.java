package ph.adamw.moose.core.util.command;

import lombok.Getter;

public class CommandSyntax {
	private final String[] pattern;

	@Getter
	private final String helpText;

	public CommandSyntax(String pattern, String helpText) {
		this.helpText = helpText;

		if(pattern.isEmpty()) {
			this.pattern = new String[0];
		} else {
			this.pattern = pattern.split(" ");
		}
	}

	public boolean syntaxMatches(String[] args) {
		if(pattern.length != args.length) {
			return false;
		}

		for(int i = 0; i < args.length; i ++) {
			if(pattern[i].startsWith("[")) {
				final CommandArgument argument = CommandArgument.fromPattern(pattern[i]);

				if (!argument.isSyntaxValid(args[i])) {
					return false;
				}
			} else {
				if (!args[i].equalsIgnoreCase(pattern[i])) {
					return false;
				}
			}
		}

		return true;
	}

	public String isDataValid(String[] args) {
		if(pattern.length != args.length) {
			// Should never be called since isDataValid *SHOULD* only ever be ran after syntaxMatches() which checks this
			return "Too few arguments given.";
		}

		for(int i = 0; i < args.length; i ++) {
			if(pattern[i].startsWith("[")) {
				final CommandArgument argument = CommandArgument.fromPattern(pattern[i]);

				if (argument.getObjectFromArg(args[i], ) == null) {
					return argument.getInvalidDataString(args[i]);
				}
			} else {
				if(!args[i].equalsIgnoreCase(pattern[i])) {
					return "Didn't understand argument: {" + args[i] + "}, expected: {" + pattern[i] + "}.";
				}
			}
		}

		return null;
	}

	public Object[] stringArgsToObjects(String[] args) {
		final Object[] objs = new Object[args.length];

		for(int i = 0; i < args.length; i ++) {
			if(pattern[i].startsWith("[")) {
				final CommandArgument argument = CommandArgument.fromPattern(pattern[i]);
				objs[i] = argument.getObjectFromArg(args[i], );
			} else {
				// Raw string (i.e. for switcher commands like /admin eco, /admin region
				objs[i] = pattern[i];
			}
		}

		return objs;
	}

	public String toHumanString() {
		final StringBuilder sb = new StringBuilder();

		for(String i : pattern) {
			sb.append(" ");
			if(i.startsWith("[")) {
				sb.append("<" + CommandArgument.fromPattern(i).toHumanString() + ">");
			} else {
				sb.append(i);
			}
		}

		return sb.toString();
	}
}
