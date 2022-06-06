FROM mcr.microsoft.com/dotnet/aspnet:6.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY NuGet.Config /root/.nuget/NuGet/
COPY ["api-user-management/api-user-management.csproj", "api-user-management/"]
RUN dotnet restore "api-user-management/api-user-management.csproj"
COPY . .
WORKDIR "/src/api-user-management"
RUN dotnet build "api-user-management.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "api-user-management.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "api-user-management.dll"]
