FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY NuGet.Config /root/.nuget/NuGet/
COPY ["api-version-logging/api-version-logging.csproj", "api-version-logging/"]
RUN dotnet restore "api-version-logging/api-version-logging.csproj"
COPY . .
WORKDIR "/src/api-version-logging"
RUN dotnet build "api-version-logging.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "api-version-logging.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "api-version-logging.dll"]
