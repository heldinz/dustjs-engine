print = dustenv.print;
quit = dustenv.quit;
readFile = dustenv.readFile;
delete arguments;

var compileString = function(source, templateName) {
	return dust.compile(source, templateName);
};

var compileFile = function(file, templateName) {
    return dust.compile(readFile(file), templateName);
}