FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY NuGet.Config /root/.nuget/NuGet/
COPY ["api-events/api-events.csproj", "api-events/"]
RUN dotnet restore "api-events/api-events.csproj"
COPY . .
WORKDIR "/src/api-events"
RUN dotnet build "api-events.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "api-events.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "api-events.dll"]
