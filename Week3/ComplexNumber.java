public class ComplexNumber {
	double real;
	double imag;

	public ComplexNumber (double iIn, double jIn) {
		real = iIn;
		imag = jIn;
	}

	public void add (ComplexNumber toAdd) {
		real += toAdd.getReal();
		imag += toAdd.getImag();
	}
	public void sub (ComplexNumber toAdd) {
		real -= toAdd.getReal();
		imag -= toAdd.getImag();
	}

	public void multiply (ComplexNumber toMult) {
		double tempReal = real * toMult.getReal() - imag * toMult.getImag();
		double tempImag = real * toMult.getImag() + imag * toMult.getReal();
		real = tempReal;
		imag = tempImag;
	}

	public void square() {
		this.multiply(this);
	}

	public double absolute() {
		return real*real + imag*imag;
	}

	public double getReal() {
		return real;
	}

	public double getImag() {
		return imag;
	}
	
	//For testing purposes
	public void printnum() {
		System.out.println(real + " + " + imag + "i");
	}
}

