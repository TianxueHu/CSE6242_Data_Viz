setwd('/Users/tianxuehu/desktop/hw2-skeleton/Q1')
rm(list = ls())

data15 <- read.csv('q1-15.csv',header=TRUE, sep=",")
data16 <- read.csv('q1-16.csv',header=TRUE, sep=",")

#country
table(data16$Region)
sum<-aggregate(data16$Total.Profit, by=list(Category=data16$Region), function(x) max(c(x)))

sum

table(data$Order.Date)



asia<-subset(data, data$Region=='Asia')
aus<-subset(data, data$Region=='Australia and Oceania')
america<-subset(data, data$Region=='Central America and the Caribbean')
eu<-subset(data, data$Region=='Central America and the Caribbean')
middle<-subset(data, data$Region=='Middle East and North Africa')
subset(data, data$Region=='Central America and the Caribbean')