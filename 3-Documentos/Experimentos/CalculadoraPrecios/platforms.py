class AWS():
	def __init__(self):
		self.freeRequests = 0
		self.freeCompute = 0

		

		self.COMPUTE_PRICE = 0.00001667
		self.REQUEST_PRICE = 0.0000002
		self.API_HTTP = 0.000001
		self.API_HTTP300M = 0.0000009

	def calculatePrice(self,requests, time, memory):
		totalRequests = requests - self.freeRequests

		reqPrice = totalRequests * self.REQUEST_PRICE
		#print(reqPrice)

		totalTime = (time / 1000)
		compute = totalTime * (memory / 1024)
		totalCompute = compute - self.freeCompute
		compPrice = totalCompute * self.COMPUTE_PRICE
		#print(compPrice)

		apiPrice = 0

		if totalRequests > 300000000:
			apiPrice = 300000000 * self.API_HTTP
			apiPrice += (totalRequests - 300000000) * self.API_HTTP300M
		else:
			apiPrice = totalRequests * self.API_HTTP
		
		#print(apiPrice)

		if reqPrice < 0:
			reqPrice = 0
		
		if compPrice < 0:
			compPrice = 0
		
		if apiPrice < 0:
			apiPrice = 0
		

		price = compPrice + reqPrice + apiPrice

		#return "--------AWS---------\nTotal Cost - $"+str(price)+"  Number of requests - "+str(requests)+"  Total Compute - "+str(compute)+"GBs"
		return price




class Google():
	def __init__(self):
		self.freeRequests = 0
		self.freeCompute = 0
		self.freeComputeCPU = 0
		self.freeEgress = 5

		self.COMPUTE_PRICE = 0.0000025
		self.COMPUTE_CPU_PRICE = 0.00001
		self.REQUEST_PRICE = 0.0000004
		self.EGRESS = 0.12

	def calculatePrice(self, requests, time, memory):
		totalRequests = requests - self.freeRequests

		reqPrice = totalRequests * self.REQUEST_PRICE
		#print(reqPrice)



		cpu = 0
		
		if memory == 128:
			cpu = 200
			
		elif memory == 256:
			cpu = 400
			
		elif memory == 512:
			cpu = 800
			
		elif memory == 1024:
			cpu = 1400
			
		elif memory == 2048:
			cpu = 2400
			
		else:
			cpu = 0
			
		
		totalTime =(time / 1000)
		compute = totalTime * (memory / 1024)
		computeCPU = totalTime * (cpu / 1000)
	
		totalCompute = compute - self.freeCompute
		compPrice = totalCompute * self.COMPUTE_PRICE
		#print(compPrice)

		totalComputeCPU = computeCPU - self.freeComputeCPU
		compCPUPrice = totalComputeCPU * self.COMPUTE_CPU_PRICE
		#print(compCPUPrice)

		if reqPrice < 0:
			reqPrice = 0
		
		if compPrice < 0:
			compPrice = 0
		
		if compCPUPrice < 0:
			compCPUPrice = 0
		

		price = compPrice + reqPrice + compCPUPrice

		#return "--------Google---------\nTotal Cost - $"+str(price)+"  Number of requests - "+str(requests)+"  Total Compute - "+str(compute)+"GBs"+"  Total CPU - "+str(computeCPU)+"GHzs"
		return price


class Azure():
	def __init__(self):
		self.freeRequests = 0
		self.freeCompute = 0
		
		
		self.COMPUTE_PRICE = 0.000016
		self.REQUEST_PRICE = 0.0000002

	
	def calculatePrice(self, requests, time, memory):
		
		
		
		totalRequests = requests - self.freeRequests
		
		reqPrice = totalRequests * self.REQUEST_PRICE
		
		
		totalTime = (time/1000)
		compute = totalTime * (memory/1024) 
		totalCompute = compute - self.freeCompute
		compPrice = totalCompute * self.COMPUTE_PRICE
	
		
		
		
		
		if reqPrice < 0:
			reqPrice = 0
		
		if compPrice < 0:
			compPrice = 0
		
		price = compPrice + reqPrice
		
		#return "--------Azure---------\nTotal Cost - $"+str(price)+"  Number of requests - "+str(requests)+"  Total Compute - "+str(compute)+"GBs"
		return price

class IBM():
	def __init__(self):
		
		self.freeCompute = 0

		

		self.COMPUTE_PRICE = 0.000017


	def calculatePrice(self,requests, time, memory):
		totalRequests = requests

		#print(reqPrice)

		totalTime = (time / 1000)
		compute = totalTime * (memory / 1024)
		totalCompute = compute - self.freeCompute
		compPrice = totalCompute * self.COMPUTE_PRICE
		#print(compPrice)

		
		#print(apiPrice)
		
		if compPrice < 0:
			compPrice = 0
		

		price = compPrice

	#	return "--------IBM---------\nTotal Cost - $"+str(price)+" Number of requests - "+str(requests)+"  Total Compute - "+str(compute)+"GBs"
		return price
